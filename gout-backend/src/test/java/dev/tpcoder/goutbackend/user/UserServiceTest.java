package dev.tpcoder.goutbackend.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import dev.tpcoder.goutbackend.auth.model.UserLogin;
import dev.tpcoder.goutbackend.auth.service.AuthService;
import dev.tpcoder.goutbackend.common.enumeration.RoleEnum;
import dev.tpcoder.goutbackend.common.exception.CredentialExistsException;
import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.user.dto.UserCreationDto;
import dev.tpcoder.goutbackend.user.dto.UserUpdateDto;
import dev.tpcoder.goutbackend.user.repository.UserRepository;
import dev.tpcoder.goutbackend.user.service.RoleService;
import dev.tpcoder.goutbackend.user.service.UserServiceImpl;
import dev.tpcoder.goutbackend.wallet.WalletService;
import dev.tpcoder.goutbackend.wallet.model.UserWallet;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import dev.tpcoder.goutbackend.user.model.User;
import dev.tpcoder.goutbackend.user.model.UserRole;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

        @InjectMocks
        private UserServiceImpl userService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private WalletService walletService;

        @Mock
        private AuthService authService;

        @Mock
        private RoleService roleService;

        @Test
        void whenGetUserDtoByIdThenSuccess() {
                var mockUser = new User(1, "Test", "Test", "0800000001");
                when(userRepository.findById(anyInt()))
                                .thenReturn(Optional.of(mockUser));

                var actual = userService.getUserDtoById(1);

                Assertions.assertEquals(1, actual.id().intValue());
                Assertions.assertEquals("Test", actual.firstName());
                Assertions.assertEquals("Test", actual.lastName());
                Assertions.assertEquals("0800000001", actual.phoneNumber());
        }

        @Test
        void whenGetUserDtoByIdButNotFoundThenFail() {
                when(userRepository.findById(anyInt()))
                                .thenReturn(Optional.empty());
                Assertions.assertThrows(
                                EntityNotFoundException.class,
                                () -> userService.getUserDtoById(1));
        }

        @Test
        void whenCreateUserThenSuccess() {
                when(authService.findCredentialByUsername(anyString()))
                                .thenReturn(Optional.empty());

                when(userRepository.save(any(User.class)))
                                .thenReturn(new User(1, "Test", "Test", "0800000001"));

                AggregateReference<User, Integer> userReference = AggregateReference.to(1);
                var mockUserLogin = new UserLogin(1, userReference, "test@test.com", "^!@%^!&*#%*@%#^&*");
                when(authService.createConsumerCredential(anyInt(), anyString(), anyString()))
                                .thenReturn(mockUserLogin);
                when(roleService.bindingNewUser(anyInt(), eq(RoleEnum.CONSUMER)))
                                .thenReturn(new UserRole(1, AggregateReference.to(1),
                                                AggregateReference.to(RoleEnum.CONSUMER.getId())));
                when(walletService.createConsumerWallet(anyInt()))
                                .thenReturn(new UserWallet(1, userReference, Instant.now(), new BigDecimal("0.00")));

                var body = new UserCreationDto(
                                "Test",
                                "Test",
                                "0800000001",
                                "test@test.com",
                                "123456789");

                var actual = userService.createUser(body);

                Assertions.assertEquals(1, actual.id().intValue());
                Assertions.assertEquals("Test", actual.firstName());
                Assertions.assertEquals("Test", actual.lastName());
                Assertions.assertEquals("0800000001", actual.phoneNumber());
        }

        @Test
        void whenCreateUserOnExistingCredentialThenFail() {
                var email = "test@test.com";
                AggregateReference<User, Integer> userReference = AggregateReference.to(1);
                var mockUserLogin = new UserLogin(1, userReference, email, "^!@%^!&*#%*@%#^&*");
                when(authService.findCredentialByUsername(anyString()))
                                .thenReturn(Optional.of(mockUserLogin));

                var body = new UserCreationDto(
                                "Test",
                                "Test",
                                "0800000001",
                                "test@test.com",
                                "123456789");

                Assertions.assertThrows(CredentialExistsException.class, () -> userService.createUser(body));
        }

        @Test
        void whenUpdateUserThenSuccess() {
                var mockUser = new User(1, "Test", "Test", "0800000001");
                when(userRepository.findById(anyInt()))
                                .thenReturn(Optional.of(mockUser));

                var updatedUser = new User(mockUser.id(), "Test1", mockUser.lastName(), mockUser.phoneNumber());
                when(userRepository.save(any(User.class)))
                                .thenReturn(updatedUser);

                var body = new UserUpdateDto(
                                "Test",
                                "Test1");
                var actual = userService.updateUser(1, body);

                Assertions.assertEquals(1, actual.id().intValue());
                Assertions.assertEquals("Test1", actual.firstName());
                Assertions.assertEquals("Test", actual.lastName());
                Assertions.assertEquals("0800000001", actual.phoneNumber());
        }

        @Test
        void whenDeleteUserThenSuccess() {
                var mockUser = new User(1, "Test", "Test", "0800000001");
                when(userRepository.findById(anyInt()))
                                .thenReturn(Optional.of(mockUser));
                doNothing().when(authService).deleteCredentialByUserId(anyInt());
                doNothing().when(walletService).deleteConsumerWalletByUserId(anyInt());
                doNothing().when(roleService).deleteRoleByUserId(anyInt());
                doNothing().when(userRepository).delete(any(User.class));
                Assertions.assertTrue(userService.deleteUserById(1));
        }
}
