package dev.tpcoder.goutbackend.wallet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.user.model.User;
import dev.tpcoder.goutbackend.wallet.model.UserWallet;
import dev.tpcoder.goutbackend.wallet.repository.UserWalletRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private UserWalletRepository userWalletRepository;

    @Test
    void whenCreateConsumerWalletThenSuccess() {
        AggregateReference<User, Integer> userReference = AggregateReference.to(1);
        Instant currentTimestamp = Instant.now();
        BigDecimal initBalance = new BigDecimal("0.00");
        var mockWallet = new UserWallet(1, userReference, currentTimestamp, initBalance);
        when(userWalletRepository.save(any(UserWallet.class)))
                .thenReturn(mockWallet);
        var actual = walletService.createConsumerWallet(1);
        Assertions.assertEquals(1, actual.id().intValue());
        Assertions.assertEquals(userReference, actual.userId());
        Assertions.assertEquals(currentTimestamp, actual.lastUpdated());
        Assertions.assertEquals(initBalance, actual.balance());
    }

    @Test
    void whenDeleteConsumerWalletThenSuccess() {
        AggregateReference<User, Integer> userReference = AggregateReference.to(1);
        var mockUserWallet = new UserWallet(1, userReference, Instant.now(), new BigDecimal("0.00"));
        when(userWalletRepository.findOneByUserId(any(AggregateReference.class)))
                .thenReturn(Optional.of(mockUserWallet));
        doNothing().when(userWalletRepository)
                .delete(any(UserWallet.class));
        Assertions.assertDoesNotThrow(() -> walletService.deleteConsumerWalletByUserId(1));
    }

    @Test
    void whenDeleteConsumerWalletButNotFoundThenFail() {
        when(userWalletRepository.findOneByUserId(any(AggregateReference.class)))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> walletService.deleteConsumerWalletByUserId(1));
    }
}
