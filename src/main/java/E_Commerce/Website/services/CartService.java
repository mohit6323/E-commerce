package E_Commerce.Website.services;

import E_Commerce.Website.dto.CartDTO;
import E_Commerce.Website.dto.CartResponseDTO;

public interface CartService {
    String addToCart(String userEmail, CartResponseDTO cartResponseDTO);
    String addToCart(Long userId, CartResponseDTO cartResponseDTO);
    String removeFromCart(Long cartItem);
    CartDTO getCartByUserId(Long userId);
    CartDTO getCartByUserEmail(String userEmail);
}
