package dev.tpcoder.goutbackend.wallet;

import java.math.BigDecimal;

import org.springframework.data.util.Pair;

import dev.tpcoder.goutbackend.booking.model.Booking;
import dev.tpcoder.goutbackend.common.enumeration.TransactionType;
import dev.tpcoder.goutbackend.wallet.dto.TopupDto;
import dev.tpcoder.goutbackend.wallet.dto.UserWalletInfoDto;
import dev.tpcoder.goutbackend.wallet.model.TourCompanyWallet;
import dev.tpcoder.goutbackend.wallet.model.UserWallet;

public interface WalletService {

    UserWallet createConsumerWallet(int userId);

    void deleteConsumerWalletByUserId(int userId);

    UserWalletInfoDto getOwnWallet(int userId);

    UserWalletInfoDto topup(TopupDto body);

    Pair<UserWallet, TourCompanyWallet> getUserWalletAndTourCompanyWallet(Booking bookingData);

    Pair<UserWallet, TourCompanyWallet> transfer(
            UserWallet userWallet,
            TourCompanyWallet tourCompanyWallet,
            BigDecimal amount,
            TransactionType type);
}
