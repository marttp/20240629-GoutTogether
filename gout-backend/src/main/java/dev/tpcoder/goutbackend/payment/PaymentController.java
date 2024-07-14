package dev.tpcoder.goutbackend.payment;

import java.awt.image.BufferedImage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.tpcoder.goutbackend.booking.dto.BookingInfoDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping(value = "/qr/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> getQrCodeById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(paymentService.generatePaymentQr(id));
    }

    @PostMapping("/{bookingId}")
    public ResponseEntity<BookingInfoDto> payment(
            @RequestHeader("idempotent-key") String idempotentKey,
            @PathVariable int bookingId) {
        return ResponseEntity.ok(paymentService.paymentOnBooking(idempotentKey, bookingId));
    }

}
