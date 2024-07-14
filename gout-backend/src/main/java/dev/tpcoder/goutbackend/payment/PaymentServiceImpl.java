package dev.tpcoder.goutbackend.payment;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.tpcoder.goutbackend.booking.dto.BookingInfoDto;
import dev.tpcoder.goutbackend.booking.model.Booking;
import dev.tpcoder.goutbackend.booking.repository.BookingRepository;
import dev.tpcoder.goutbackend.common.enumeration.BookingStatusEnum;
import dev.tpcoder.goutbackend.common.enumeration.QrCodeStatus;
import dev.tpcoder.goutbackend.common.enumeration.TransactionType;
import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.qrcode.QrCodeService;
import dev.tpcoder.goutbackend.tour.service.TourCountService;
import dev.tpcoder.goutbackend.wallet.WalletService;

@Service
public class PaymentServiceImpl implements PaymentService {

        private final QrCodeService qrCodeService;
        private final BookingRepository bookingRepository;
        private final WalletService walletService;
        private final TransactionRepository transactionRepository;
        private final TourCountService tourCountService;
        private final int tourPrice;

        public PaymentServiceImpl(BookingRepository bookingRepository, QrCodeService qrCodeService,
                        TransactionRepository transactionRepository, WalletService walletService,
                        @Value(value = "${booking.tour-price}") int tourPrice,
                        TourCountService tourCountService) {
                this.bookingRepository = bookingRepository;
                this.qrCodeService = qrCodeService;
                this.transactionRepository = transactionRepository;
                this.walletService = walletService;
                this.tourPrice = tourPrice;
                this.tourCountService = tourCountService;
        }

        @Override
        public BufferedImage generatePaymentQr(int id) throws Exception {
                return qrCodeService.generateQrById(id);
        }

        @Transactional
        @Override
        public BookingInfoDto paymentOnBooking(String idempotentKey, int bookingId) {
                // idempotentKey - use in transaction
                var bookingData = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                String.format("BookingId: %d not found", bookingId)));
                var wallets = walletService.getUserWalletAndTourCompanyWallet(bookingData);
                var userWallet = wallets.getFirst();
                var tourCompanyWallet = wallets.getSecond();
                // UserWallet -
                // TourCompanyWallet +
                walletService.transfer(
                                userWallet,
                                tourCompanyWallet,
                                BigDecimal.valueOf(tourPrice),
                                TransactionType.BOOKING);
                var newTransaction = TransactionUtil.generateBookingTransaction(
                                idempotentKey,
                                bookingId,
                                userWallet.userId().getId(),
                                tourCompanyWallet.tourCompanyId().getId(),
                                Instant.now(),
                                BigDecimal.valueOf(tourPrice));
                transactionRepository.save(newTransaction);
                var qrCodeReference = qrCodeService.updatedQrStatus(bookingId, QrCodeStatus.EXPIRED);

                // Update booking to completed
                var prepareUpdateBooking = new Booking(
                                bookingData.id(),
                                bookingData.userId(),
                                bookingData.tourId(),
                                BookingStatusEnum.COMPLETED.name(),
                                bookingData.bookingDate(),
                                Instant.now(),
                                idempotentKey);
                bookingRepository.save(prepareUpdateBooking);
                // Update tour count
                tourCountService.incrementTourCount(bookingData.tourId().getId());
                return new BookingInfoDto(
                                bookingData.id(),
                                bookingData.userId().getId(),
                                bookingData.tourId().getId(),
                                BookingStatusEnum.COMPLETED.name(),
                                qrCodeReference.id());
        }

        @Transactional
        @Override
        public void refundOnBooking(String idempotentKey, int bookingId) {
                var bookingData = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                String.format("BookingId: %d not found", bookingId)));
                var wallets = walletService.getUserWalletAndTourCompanyWallet(bookingData);
                var userWallet = wallets.getFirst();
                var tourCompanyWallet = wallets.getSecond();
                // TourCompanyWallet -
                // UserWallet +
                walletService.transfer(
                                userWallet,
                                tourCompanyWallet,
                                BigDecimal.valueOf(tourPrice),
                                TransactionType.REFUND);
                var newTransaction = TransactionUtil.generateRefundTransaction(
                                idempotentKey,
                                bookingId,
                                userWallet.userId().getId(),
                                tourCompanyWallet.tourCompanyId().getId(),
                                Instant.now(),
                                BigDecimal.valueOf(tourPrice));
                transactionRepository.save(newTransaction);
        }
}
