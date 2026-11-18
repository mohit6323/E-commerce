package E_Commerce.Website;

import E_Commerce.Website.dto.LoginRequestDTO;
import E_Commerce.Website.dto.RegisterDTO;
import E_Commerce.Website.entity.Role;
import E_Commerce.Website.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void registerCustomer_withValidPayload_returnsSuccessMessage() throws Exception {
        RegisterDTO request = new RegisterDTO("Customer User", "Password123", "customer@example.com", "+1234567890", "Customer Address");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        assertThat(userRepository.findByEmail("customer@example.com")).isPresent();
        assertThat(userRepository.findByEmail("customer@example.com").orElseThrow().getRole()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void loginCustomer_withValidCredentials_returnsSuccessMessage() throws Exception {
        createUser(Role.CUSTOMER);
        String email = userRepository.findAll().get(0).getEmail();

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new LoginRequestDTO(email, "Password123"))))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged in successfully"));
    }

    @Test
    void loginCustomer_withWrongPassword_returnsBadRequest() throws Exception {
        createUser(Role.CUSTOMER);
        String email = userRepository.findAll().get(0).getEmail();

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new LoginRequestDTO(email, "WrongPassword"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
