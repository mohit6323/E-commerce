package E_Commerce.Website.services.impl;

import E_Commerce.Website.dto.LoginRequestDTO;
import E_Commerce.Website.dto.RegisterDTO;
import E_Commerce.Website.entity.Role;
import E_Commerce.Website.entity.User;
import E_Commerce.Website.repository.UserRepository;
import E_Commerce.Website.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String register(RegisterDTO registerDTO) {
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            log.warn("Admin registration rejected for duplicate email {}", registerDTO.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhoneNumber(registerDTO.getPhone());
        user.setAddress(registerDTO.getAddress());
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        log.info("Admin account registered for {}", user.getEmail());
        return "User registered successfully";
    }

    @Override
    public String login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            log.warn("Admin login failed for {}", loginRequestDTO.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        log.info("Admin login succeeded for {}", user.getEmail());
        return "User logged in successfully";
    }
}
