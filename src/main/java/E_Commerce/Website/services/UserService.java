package E_Commerce.Website.services;


import E_Commerce.Website.dto.LoginRequestDTO;
import E_Commerce.Website.dto.RegisterDTO;

public interface UserService {
    String register(RegisterDTO registerDTO);

    String login(LoginRequestDTO loginRequestDTO);




}