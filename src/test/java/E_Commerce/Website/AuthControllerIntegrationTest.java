package E_Commerce.Website;

import E_Commerce.Website.dto.AuthResponseDTO;
import E_Commerce.Website.dto.LoginRequestDTO;
import E_Commerce.Website.dto.RefreshTokenRequestDTO;
import E_Commerce.Website.dto.RegisterDTO;
import E_Commerce.Website.entity.Role;
import E_Commerce.Website.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void registerAdmin_withValidPayload_returnsSuccessMessage() throws Exception {
        RegisterDTO request = new RegisterDTO("Admin User", "Password123", "admin@example.com", "+1234567890", "Admin Address");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        assertThat(userRepository.findByEmail("admin@example.com")).isPresent();
        assertThat(userRepository.findByEmail("admin@example.com").orElseThrow().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void loginAdmin_withValidCredentials_returnsAccessAndRefreshTokens() throws Exception {
        createUser(Role.ADMIN);
        String email = userRepository.findAll().get(0).getEmail();
        LoginRequestDTO request = new LoginRequestDTO(email, "Password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void refreshToken_withValidToken_returnsNewAccessToken() throws Exception {
        createUser(Role.ADMIN);
        String email = userRepository.findAll().get(0).getEmail();
        LoginRequestDTO loginRequest = new LoginRequestDTO(email, "Password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDTO authResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthResponseDTO.class);

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new RefreshTokenRequestDTO(authResponse.getRefreshToken()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").value(authResponse.getRefreshToken()))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void registerAdmin_withInvalidPayload_returnsValidationErrors() throws Exception {
        RegisterDTO request = new RegisterDTO("", "123", "invalid-email", "abc", "");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.password").exists());
    }
}
