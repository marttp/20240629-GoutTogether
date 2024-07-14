package dev.tpcoder.goutbackend.qrcode;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface QrCodeReferenceRepository extends CrudRepository<QrCodeReference, Integer> {

    Optional<QrCodeReference> findOneByBookingId(Integer bookingId);

}
