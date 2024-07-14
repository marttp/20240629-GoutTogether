package dev.tpcoder.goutbackend.payment;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import java.math.BigDecimal;
import java.time.Instant;
import dev.tpcoder.goutbackend.common.enumeration.TransactionType;

public class TransactionUtil {

    private TransactionUtil() {}

    public static Transaction generateTopupTransaction(
            String idempotentKey,
            Integer userId,
            Instant timestamp,
            BigDecimal amount) {
        return new Transaction(
                null,
                AggregateReference.to(userId),
                null,
                timestamp,
                amount,
                TransactionType.TOPUP.name(),
                idempotentKey, 
                null
        );
    }

    public static Transaction generateBookingTransaction(
            String idempotentKey,
            Integer bookingId,
            Integer userId,
            Integer tourCompanyId,
            Instant timestamp,
            BigDecimal amount) {
        return new Transaction(
                null,
                AggregateReference.to(userId),
                AggregateReference.to(tourCompanyId),
                timestamp,
                amount,
                TransactionType.BOOKING.name(),
                idempotentKey,
                bookingId
        );
    }

    public static Transaction generateRefundTransaction(
            String idempotentKey,
            Integer bookingId,
            Integer userId,
            Integer tourCompanyId,
            Instant timestamp,
            BigDecimal amount) {
        return new Transaction(
                null,
                AggregateReference.to(userId),
                AggregateReference.to(tourCompanyId),
                timestamp,
                amount,
                TransactionType.REFUND.name(),
                idempotentKey, 
                bookingId
        );
    }
}
