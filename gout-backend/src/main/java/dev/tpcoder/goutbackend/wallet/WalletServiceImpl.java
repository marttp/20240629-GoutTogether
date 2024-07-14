package dev.tpcoder.goutbackend.wallet;

import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.tpcoder.goutbackend.booking.model.Booking;
import dev.tpcoder.goutbackend.common.enumeration.TransactionType;
import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.payment.Transaction;
import dev.tpcoder.goutbackend.payment.TransactionRepository;
import dev.tpcoder.goutbackend.payment.TransactionUtil;
import dev.tpcoder.goutbackend.tour.repository.TourRepository;
import dev.tpcoder.goutbackend.user.model.User;
import dev.tpcoder.goutbackend.wallet.dto.TopupDto;
import dev.tpcoder.goutbackend.wallet.dto.UserWalletInfoDto;
import dev.tpcoder.goutbackend.wallet.model.TourCompanyWallet;
import dev.tpcoder.goutbackend.wallet.model.UserWallet;
import dev.tpcoder.goutbackend.wallet.repository.TourCompanyWalletRepository;
import dev.tpcoder.goutbackend.wallet.repository.UserWalletRepository;

@Service
public class WalletServiceImpl implements WalletService {

    private final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final UserWalletRepository userWalletRepository;
    private final TransactionRepository transactionRepository;
    private final TourCompanyWalletRepository tourCompanyWalletRepository;
    private final TourRepository tourRepository;

    public WalletServiceImpl(
            TourCompanyWalletRepository tourCompanyWalletRepository,
            TourRepository tourRepository,
            TransactionRepository transactionRepository,
            UserWalletRepository userWalletRepository) {
        this.tourCompanyWalletRepository = tourCompanyWalletRepository;
        this.tourRepository = tourRepository;
        this.transactionRepository = transactionRepository;
        this.userWalletRepository = userWalletRepository;
    }

    @Override
    public UserWallet createConsumerWallet(int userId) {
        AggregateReference<User, Integer> userReference = AggregateReference.to(userId);
        Instant currentTimestamp = Instant.now();
        BigDecimal initBalance = new BigDecimal("0.00");
        var wallet = new UserWallet(null, userReference, currentTimestamp, initBalance);
        var newWallet = userWalletRepository.save(wallet);
        logger.info("Created wallet for user: {}", userId);
        return newWallet;
    }

    @Override
    public void deleteConsumerWalletByUserId(int userId) {
        AggregateReference<User, Integer> userReference = AggregateReference.to(userId);
        var wallet = userWalletRepository.findOneByUserId(userReference)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Wallet for user Id: %d not found", userId)));
        userWalletRepository.delete(wallet);
    }

    @Override
    @Transactional
    public UserWalletInfoDto topup(TopupDto body) {
        var now = Instant.now();
        var idempotentKey = body.idempotentKey();
        var userId = body.userId();
        var userWallet = getWalletByUserId(userId);
        var optionalHistoricalTransaction = transactionRepository.findOneByIdempotentKey(idempotentKey);
        // If Idempotent Key exists -> Just return existing information
        if (optionalHistoricalTransaction.isPresent()) {
            return new UserWalletInfoDto(userWallet.userId().getId(), userWallet.balance());
        }
        var newTransaction = TransactionUtil.generateTopupTransaction(idempotentKey, userId, now, body.amount());
        transactionRepository.save(newTransaction);
        var updatedBalance = userWallet.balance().add(body.amount());
        var updatedTopupBalance = new UserWallet(userWallet.id(), userWallet.userId(), now, updatedBalance);
        var updatedWallet = userWalletRepository.save(updatedTopupBalance);
        return new UserWalletInfoDto(updatedWallet.userId().getId(), updatedWallet.balance());
    }

    @Override
    public UserWalletInfoDto getOwnWallet(int userId) {
        var userWallet = getWalletByUserId(userId);
        return new UserWalletInfoDto(userWallet.userId().getId(), userWallet.balance());
    }

    private UserWallet getWalletByUserId(int userId) {
        return userWalletRepository.findOneByUserId(AggregateReference.to(userId))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Wallet for userId: %d not found", userId)));
    }

    @Override
    public Pair<UserWallet, TourCompanyWallet> getUserWalletAndTourCompanyWallet(Booking bookingData) {
        var userId = bookingData.userId();
        var tourId = bookingData.tourId();
        var userWallet = userWalletRepository.findOneByUserId(userId)
                .orElseThrow(EntityNotFoundException::new);
        var tourInfo = tourRepository.findById(tourId.getId())
                .orElseThrow(EntityNotFoundException::new);
        var tourCompanyWallet = tourCompanyWalletRepository
                .findOneByTourCompanyId(tourInfo.tourCompanyId())
                .orElseThrow(EntityNotFoundException::new);
        return Pair.of(userWallet, tourCompanyWallet);
    }

    @Override
    public Pair<UserWallet, TourCompanyWallet> transfer(
            UserWallet userWallet,
            TourCompanyWallet tourCompanyWallet,
            BigDecimal amount,
            TransactionType type) {
        return switch (type) {
            case TransactionType.BOOKING -> {
                var prepareUserWallet = new UserWallet(
                        userWallet.id(),
                        userWallet.userId(),
                        Instant.now(),
                        userWallet.balance().subtract(amount)
                );
                var prepaTourCompanyWallet = new TourCompanyWallet(
                        tourCompanyWallet.id(),
                        tourCompanyWallet.tourCompanyId(),
                        Instant.now(),
                        tourCompanyWallet.balance().add(amount)
                );
                // Don't forget to apply pessimistic lock here
                var updateUserWallet = userWalletRepository.save(prepareUserWallet);
                var updateTourCompanyWallet = tourCompanyWalletRepository.save(prepaTourCompanyWallet);
                yield Pair.of(updateUserWallet, updateTourCompanyWallet);
            }
            case TransactionType.REFUND -> {
                var prepareUserWallet = new UserWallet(
                        userWallet.id(),
                        userWallet.userId(),
                        Instant.now(),
                        userWallet.balance().add(amount)
                );
                var prepaTourCompanyWallet = new TourCompanyWallet(
                        tourCompanyWallet.id(),
                        tourCompanyWallet.tourCompanyId(),
                        Instant.now(),
                        tourCompanyWallet.balance().subtract(amount)
                );
                // Don't forget to apply pessimistic lock here
                var updateUserWallet = userWalletRepository.save(prepareUserWallet);
                var updateTourCompanyWallet = tourCompanyWalletRepository.save(prepaTourCompanyWallet);
                yield Pair.of(updateUserWallet, updateTourCompanyWallet);
            }
            default -> {
                throw new IllegalArgumentException("Invalid Transaction Type");
            }
        };
    }
}
