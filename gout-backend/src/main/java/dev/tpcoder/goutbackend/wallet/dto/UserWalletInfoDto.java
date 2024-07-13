package dev.tpcoder.goutbackend.wallet.dto;

import java.math.BigDecimal;

public record UserWalletInfoDto(
        Integer userId,
        BigDecimal balance) {

}
