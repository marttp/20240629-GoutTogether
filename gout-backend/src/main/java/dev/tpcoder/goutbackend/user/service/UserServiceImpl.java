package dev.tpcoder.goutbackend.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.tpcoder.goutbackend.auth.AuthService;
import dev.tpcoder.goutbackend.common.exception.EntityNotFound;
import dev.tpcoder.goutbackend.user.dto.UserCreationDto;
import dev.tpcoder.goutbackend.user.dto.UserInfoDto;
import dev.tpcoder.goutbackend.user.dto.UserUpdateDto;
import dev.tpcoder.goutbackend.user.model.User;
import dev.tpcoder.goutbackend.user.repository.UserRepository;
import dev.tpcoder.goutbackend.wallet.WalletService;

@Service
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final AuthService authService;

    public UserServiceImpl(AuthService authService, UserRepository userRepository, WalletService walletService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.walletService = walletService;
    }

    @Override
    public UserInfoDto getUserDtoById(int id) {
        var user = getUserById(id);
        return new UserInfoDto(user.id(), user.firstName(), user.lastName(), user.phoneNumber());
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound(String.format("User Id: %d not found", id)));
    }

    // Create user + login credential + wallet
    @Override
    public UserInfoDto createUser(UserCreationDto body) {
        // 1. Find existing credential
        // 2. Create user
        // 3. Create credential
        // 4. Create wallet for user
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    // Update user
    @Override
    public UserInfoDto updateUser(int id, UserUpdateDto body) {
        var user = getUserById(id);
        var prepareUser = new User(user.id(), body.firstName(), body.lastName(), user.phoneNumber());
        var updatedUser = userRepository.save(prepareUser);
        return new UserInfoDto(updatedUser.id(), updatedUser.firstName(), updatedUser.lastName(),
                updatedUser.phoneNumber());
    }

    // Delete user + credential & wallet removal
    @Override
    public boolean deleteUserById(int id) {
        // 1. Find existing user
        // 2. Find every resource under this userId and delete
        throw new UnsupportedOperationException("Unimplemented method 'deleteUserById'");
    }
}
