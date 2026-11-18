package E_Commerce.Website.services.impl;


import E_Commerce.Website.dto.CartDTO;
import E_Commerce.Website.dto.CartItemDTO;
import E_Commerce.Website.dto.CartResponseDTO;
import E_Commerce.Website.entity.Cart;
import E_Commerce.Website.entity.CartItem;
import E_Commerce.Website.exception.ResourceNotFoundException;
import E_Commerce.Website.repository.*;
import E_Commerce.Website.repository.ProductRepository;
import E_Commerce.Website.repository.UserRepository;
import E_Commerce.Website.services.CartService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public String addToCart(String userEmail, CartResponseDTO cartResponseDTO) {
        var user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        return addToCart(user.getId(), cartResponseDTO);
    }

    @Override
    @Transactional
    public String addToCart(Long userId, CartResponseDTO cartResponseDTO) {

        var user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));

        var cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        var product = productRepository.findById
                (cartResponseDTO.getProductId()).orElseThrow(() ->
                new ResourceNotFoundException("Product not found"));

        Optional<CartItem> existingCartItem =
                cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartResponseDTO.getQuantity());
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartResponseDTO.getQuantity());
            cartItem.setPrice(product.getPrice());
            cartItemRepository.save(cartItem);
        }
        log.info("Added product {} to cart for user {}", product.getId(), userId);
        return "Product added to cart successfully";
    }

    @Override
    @Transactional
    public String removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItemRepository.delete(cartItem);
        log.info("Removed cart item {}", cartItemId);
        return "Product removed from cart successfully";
    }

    @Override
    public CartDTO getCartByUserEmail(String userEmail) {
        var user = userRepository.findByEmail(userEmail).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        return getCartByUserId(user.getId());
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("Cart not found for user id: " + userId));
        return toCartDto(cart);
    }

    private CartDTO toCartDto(Cart cart) {
        List<CartItemDTO> itemDTOS = cart.getCartItems() == null
                ? List.of()
                : cart.getCartItems().stream()
                .map(this::toCartItemDto)
                .toList();
        double totalAmount = itemDTOS.stream()
                .mapToDouble(item -> item.getLineTotal() == null ? 0.0 : item.getLineTotal())
                .sum();
        return new CartDTO(cart.getId(), cart.getUser().getId(), itemDTOS, totalAmount);
    }

    private CartItemDTO toCartItemDto(CartItem cartItem) {
        Double lineTotal = cartItem.getPrice() * cartItem.getQuantity();
        return new CartItemDTO(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getPrice(),
                lineTotal
        );
    }
}
