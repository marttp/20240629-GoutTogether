package dev.tpcoder.goutbackend.wallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.tpcoder.goutbackend.wallet.dto.TopupDto;
import dev.tpcoder.goutbackend.wallet.dto.TourCompanyWalletInfoDto;
import dev.tpcoder.goutbackend.wallet.dto.UserWalletInfoDto;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final Logger logger = LoggerFactory.getLogger(WalletController.class);
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // User -> See own wallet
    @GetMapping("/me")
    public ResponseEntity<UserWalletInfoDto> getOwnWallet(Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        var userId = jwt.getClaimAsString("sub");
        var wallet = walletService.getOwnWallet(Integer.valueOf(userId));
        return ResponseEntity.ok(wallet);
    }

    // User -> Topup (Assume do via application, bank deduct on the background)
    @PostMapping("/topup")
    public ResponseEntity<UserWalletInfoDto> topup(
            @RequestHeader("idempotent-key") String idempotentKey,
            @RequestBody @Validated TopupDto body,
            Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        var userId = jwt.getClaimAsString("sub");
        var recreateBody = new TopupDto(body.amount(), Integer.valueOf(userId), idempotentKey);
        var result = walletService.topup(recreateBody);
        return ResponseEntity.ok(result);
    }

    // Company -> See own wallet
    public ResponseEntity<TourCompanyWalletInfoDto> getOwnWalletForCompany() {
        return ResponseEntity.ok().build();
    }

    // Company -> pay to bank account
    public ResponseEntity<TourCompanyWalletInfoDto> payToOwnBankAccount() {
        logger.info("Assume pay to bank");
        return ResponseEntity.ok().build();
    }

}
