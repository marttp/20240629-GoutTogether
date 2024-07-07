package dev.tpcoder.goutbackend.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.tpcoder.goutbackend.common.exception.EntityNotFoundException;
import dev.tpcoder.goutbackend.user.model.User;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    @Spy
    private AuthServiceImpl authService;

    @Mock
    private UserLoginRepository userLoginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void whenFindByUserNameThenSuccess() {
        var email = "test@test.com";
        AggregateReference<User, Integer> userReference = AggregateReference.to(1);
        var mockUserLogin = new UserLogin(1, userReference, email, "^!@%^!&*#%*@%#^&*");
        when(userLoginRepository.findOneByEmail(anyString()))
                .thenReturn(Optional.of(mockUserLogin));

        var actual = authService.findCredentialByUsername(email);

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(1, actual.get().id().intValue());
        Assertions.assertEquals(userReference, actual.get().userId());
        Assertions.assertEquals(email, actual.get().email());
        Assertions.assertEquals("^!@%^!&*#%*@%#^&*", actual.get().password());
    }

    @Test
    void whenFindByUserIdThenSuccess() {
        var email = "test@test.com";
        AggregateReference<User, Integer> userReference = AggregateReference.to(1);
        var mockUserLogin = new UserLogin(1, userReference, email, "^!@%^!&*#%*@%#^&*");
        when(userLoginRepository.findOneByUserId(any(AggregateReference.class)))
                .thenReturn(Optional.of(mockUserLogin));

        var actual = authService.findCredentialByUserId(1);

        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(1, actual.get().id().intValue());
        Assertions.assertEquals(userReference, actual.get().userId());
        Assertions.assertEquals(email, actual.get().email());
        Assertions.assertEquals("^!@%^!&*#%*@%#^&*", actual.get().password());
    }

    @Test
    void whenCreateCredentialThenSuccess() {
        AggregateReference<User, Integer> userReference = AggregateReference.to(1);
        var mockEncryptedPassword = "^!@%^!&*#%*@%#^&*";
        when(passwordEncoder.encode(anyString()))
                .thenReturn(mockEncryptedPassword);

        var mockUserCredential = new UserLogin(1, userReference, "test@test.com", mockEncryptedPassword);
        when(userLoginRepository.save(any(UserLogin.class)))
                .thenReturn(mockUserCredential);

        var actual = authService.createConsumerCredential(1, "test@test.com", mockEncryptedPassword);

        Assertions.assertEquals(1, actual.id().intValue());
        Assertions.assertEquals(userReference, actual.userId());
        Assertions.assertEquals("test@test.com", actual.email());
        Assertions.assertEquals("^!@%^!&*#%*@%#^&*", actual.password());
    }

    @Test
    void whenDeleteCredentialThenSuccess() {
        AggregateReference<User, Integer> userReference = AggregateReference.to(1);
        var mockUserLogin = new UserLogin(1, userReference, "test@test.com", "^!@%^!&*#%*@%#^&*");
        when(authService.findCredentialByUserId(anyInt()))
                .thenReturn(Optional.of(mockUserLogin));
        doNothing().when(userLoginRepository).delete(any(UserLogin.class));
        Assertions.assertDoesNotThrow(() -> authService.deleteCredentialByUserId(1));
    }

    @Test
    void whenDeleteCredentialButNotFoundThenFail() {
        when(authService.findCredentialByUserId(anyInt()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> authService.deleteCredentialByUserId(1));
    }
}
