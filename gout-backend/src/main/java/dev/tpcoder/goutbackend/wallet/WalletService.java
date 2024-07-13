package dev.tpcoder.goutbackend.wallet;

import dev.tpcoder.goutbackend.wallet.dto.TopupDto;
import dev.tpcoder.goutbackend.wallet.dto.UserWalletInfoDto;
import dev.tpcoder.goutbackend.wallet.model.UserWallet;

public interface WalletService {

    UserWallet createConsumerWallet(int userId);

    void deleteConsumerWalletByUserId(int userId);

    UserWalletInfoDto getOwnWallet(int userId);

    UserWalletInfoDto topup(TopupDto body);
}
