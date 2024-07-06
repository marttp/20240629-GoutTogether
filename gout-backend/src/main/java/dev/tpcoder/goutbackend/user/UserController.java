package dev.tpcoder.goutbackend.user;

import org.springframework.web.bind.annotation.RestController;

import dev.tpcoder.goutbackend.user.dto.UserCreationDto;
import dev.tpcoder.goutbackend.user.dto.UserInfoDto;
import dev.tpcoder.goutbackend.user.dto.UserUpdateDto;
import dev.tpcoder.goutbackend.user.service.UserService;

import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDto> getUserById(@PathVariable Integer id) {
        var result = userService.getUserDtoById(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<UserInfoDto> createUser(@RequestBody @Validated UserCreationDto body) {
        var newUser = userService.createUser(body);
        var location = String.format("http://localhost/api/v1/users/%d", newUser.id());
        return ResponseEntity.created(URI.create(location)).body(newUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserInfoDto> updateUser(@PathVariable Integer id,
            @RequestBody @Validated UserUpdateDto body) {
        var result = userService.updateUser(id, body);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Integer id) {
        userService.deleteUserById(id);
        logger.info("UserId: {} has been deleted", id);
        return ResponseEntity.ok(true);
    }

}