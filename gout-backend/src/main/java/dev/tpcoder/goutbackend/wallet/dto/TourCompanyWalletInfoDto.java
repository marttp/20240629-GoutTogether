package dev.tpcoder.goutbackend.wallet.dto;

import java.math.BigDecimal;

public record TourCompanyWalletInfoDto(
        Integer tourCompanyId,
        BigDecimal balance) {

}
