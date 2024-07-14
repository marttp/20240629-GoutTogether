package dev.tpcoder.goutbackend.booking.service;

import java.time.Instant;
import java.util.Objects;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.tpcoder.goutbackend.booking.dto.BookingInfoDto;
import dev.tpcoder.goutbackend.booking.dto.CancelBookingDto;
import dev.tpcoder.goutbackend.booking.dto.RequestBookingDto;
import dev.tpcoder.goutbackend.booking.model.Booking;
import dev.tpcoder.goutbackend.booking.repository.BookingRepository;
import dev.tpcoder.goutbackend.common.enumeration.BookingStatusEnum;
import dev.tpcoder.goutbackend.common.exception.BookingExistsException;
import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.common.exception.UserIdMismatchException;
import dev.tpcoder.goutbackend.payment.PaymentService;
import dev.tpcoder.goutbackend.qrcode.QrCodeService;
import dev.tpcoder.goutbackend.tour.service.TourCountService;

@Service
public class BookingServiceImpl implements BookingService {

        private final BookingRepository bookingRepository;
        private final TourCountService tourCountService;
        private final QrCodeService qrCodeService;
        private final PaymentService paymentService;

        public BookingServiceImpl(BookingRepository bookingRepository, PaymentService paymentService,
                        QrCodeService qrCodeService, TourCountService tourCountService) {
                this.bookingRepository = bookingRepository;
                this.paymentService = paymentService;
                this.qrCodeService = qrCodeService;
                this.tourCountService = tourCountService;
        }

        @Transactional
        @Override
        public BookingInfoDto bookTour(Authentication authentication, RequestBookingDto body) {
                var jwt = (Jwt) authentication.getPrincipal();
                var userId = jwt.getClaimAsString("sub");
                if (!Objects.equals(Integer.valueOf(userId), body.userId())) {
                        throw new UserIdMismatchException("User id mismatch between credential and payload");
                }
                var idempotentKey = body.idempotentKey();
                var existingBooking = bookingRepository.findOneByUserIdAndTourId(
                                AggregateReference.to(body.userId()),
                                AggregateReference.to(body.tourId()));
                if (existingBooking.isPresent()) {
                        var data = existingBooking.get();
                        if (data.state().equals(BookingStatusEnum.COMPLETED.name())) {
                                throw new BookingExistsException(
                                                String.format("UserId: %d already booked TourId: %d",
                                                                data.userId().getId(), data.tourId().getId()));
                        }
                        return new BookingInfoDto(
                                        data.id(),
                                        data.userId().getId(),
                                        data.tourId().getId(),
                                        data.state(),
                                        null);
                }
                var now = Instant.now();
                var newBooking = new Booking(
                                null,
                                AggregateReference.to(body.userId()),
                                AggregateReference.to(body.tourId()),
                                BookingStatusEnum.PENDING.name(),
                                now,
                                now,
                                idempotentKey);
                var entity = bookingRepository.save(newBooking);
                // Generate QR Code
                var qrCodeForReference = qrCodeService.generateQrForBooking(entity.id());
                return new BookingInfoDto(
                                entity.id(),
                                entity.userId().getId(),
                                entity.tourId().getId(),
                                entity.state(),
                                qrCodeForReference.id());
        }

        @Transactional
        @Override
        public BookingInfoDto cancelTour(Authentication authentication, CancelBookingDto body) {
                var jwt = (Jwt) authentication.getPrincipal();
                var userId = jwt.getClaimAsString("sub");
                if (!Objects.equals(Integer.valueOf(userId), body.userId())) {
                        throw new UserIdMismatchException("User id mismatch between credential and payload");
                }
                var existsBooking = bookingRepository.findById(body.bookingId())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                String.format("BookingId: %d not found", body.bookingId())));
                // Update tour count
                tourCountService.decrementTourCount(existsBooking.tourId().getId());
                // Refund payment
                paymentService.refundOnBooking(body.idempotentKey(), body.bookingId());
                // Delete booking for the user
                bookingRepository.deleteById(body.bookingId());
                return new BookingInfoDto(
                                existsBooking.id(),
                                existsBooking.userId().getId(),
                                existsBooking.tourId().getId(),
                                BookingStatusEnum.CANCELED.name(),
                                null);
        }
}
