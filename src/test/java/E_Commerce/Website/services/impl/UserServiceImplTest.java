package E_Commerce.Website.services.impl;

import E_Commerce.Website.dto.LoginRequestDTO;
import E_Commerce.Website.dto.RegisterDTO;
import E_Commerce.Website.entity.User;
import E_Commerce.Website.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_whenValid_savesCustomerUser() {
        RegisterDTO request = new RegisterDTO("Customer", "Password123", "customer@example.com", "+1234567890", "Address");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");

        String response = userService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(response).isEqualTo("User registered successfully");
        assertThat(captor.getValue().getRole().name()).isEqualTo("CUSTOMER");
    }

    @Test
    void login_whenUserIsMissing_throwsIllegalArgumentException() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(new LoginRequestDTO("customer@example.com", "Password123")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid email or password");
    }
}
