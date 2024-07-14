package dev.tpcoder.goutbackend.qrcode;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("qr_code_reference")
public record QrCodeReference(
        @Id Integer id,
        Integer bookingId,
        String content,
        String status) {

}
