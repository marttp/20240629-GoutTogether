package dev.tpcoder.goutbackend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.tpcoder.goutbackend.user.dto.UserInfoDto;
import dev.tpcoder.goutbackend.user.dto.UserUpdateDto;
import dev.tpcoder.goutbackend.user.service.UserService;

@RestController
@RequestMapping("/api/v1/me")
public class UserSelfManagedController {

    private final UserService userService;

    public UserSelfManagedController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserInfoDto> getUserById(Authentication authentication) {
        var result = userService.getUserDtoById(getMyId(authentication));
        return ResponseEntity.ok(result);
    }

    @PatchMapping
    public ResponseEntity<UserInfoDto> updateUser(
            @RequestBody @Validated UserUpdateDto body,
            Authentication authentication) {
        var result = userService.updateUser(getMyId(authentication), body);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteUser(Authentication authentication) {
        userService.deleteUserById(getMyId(authentication));
        return ResponseEntity.ok(true);
    }

    private int getMyId(Authentication authentication) {
        var jwt = (Jwt) authentication.getPrincipal();
        return Integer.parseInt(jwt.getClaimAsString("sub"));
    }
}
