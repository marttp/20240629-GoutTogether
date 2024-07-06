package dev.tpcoder.goutbackend.wallet;

import dev.tpcoder.goutbackend.wallet.model.UserWallet;

public interface WalletService {

    UserWallet createConsumerWallet(int userId);

    void deleteConsumerWalletByUserId(int userId);
}
