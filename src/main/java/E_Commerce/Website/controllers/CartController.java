package E_Commerce.Website.controllers;

import E_Commerce.Website.dto.CartDTO;
import E_Commerce.Website.dto.CartResponseDTO;
import E_Commerce.Website.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Customer cart operations")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get cart details for authenticated user")
    @GetMapping
    public CartDTO getCart(Authentication authentication) {
        String userEmail = authentication.getName();
        return cartService.getCartByUserEmail(userEmail);
    }

    @Operation(summary = "Add an item to authenticated user's cart")
    @PostMapping("/add")
    public String addToCart(Authentication authentication, @Valid @RequestBody CartResponseDTO cartResponseDTO) {
        String userEmail = authentication.getName();
        return cartService.addToCart(userEmail, cartResponseDTO);
    }

    @Operation(summary = "Remove an item from the cart")
    @DeleteMapping("/remove/{cartItemId}")
    public String removeFromCart(@PathVariable Long cartItemId) {
        return cartService.removeFromCart(cartItemId);
    }

}
