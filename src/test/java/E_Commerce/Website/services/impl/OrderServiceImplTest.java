package E_Commerce.Website.services.impl;

import E_Commerce.Website.entity.Cart;
import E_Commerce.Website.entity.CartItem;
import E_Commerce.Website.entity.Order;
import E_Commerce.Website.entity.Product;
import E_Commerce.Website.entity.User;
import E_Commerce.Website.exception.ResourceNotFoundException;
import E_Commerce.Website.repository.CartItemRepository;
import E_Commerce.Website.repository.CartRepository;
import E_Commerce.Website.repository.OrderItemRepository;
import E_Commerce.Website.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void placeOrder_whenCartHasItems_placesOrderAndClearsCart() {
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(4L);
        product.setPrice(75.0);
        CartItem cartItem = new CartItem(2L, null, product, 2, 75.0);
        Cart cart = new Cart();
        cart.setId(3L);
        cart.setUser(user);
        cart.setCartItems(new ArrayList<>());
        cart.getCartItems().add(cartItem);
        cartItem.setCart(cart);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String response = orderService.placeOrder(1L);

        assertThat(response).isEqualTo("Order placed successfully ");
        assertThat(cart.getCartItems()).isEmpty();
        verify(orderItemRepository).save(any());
        verify(cartItemRepository).deleteAll(anyList());
        verify(cartRepository).save(cart);
    }

    @Test
    void placeOrder_whenCartIsMissing_throwsResourceNotFoundException() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.placeOrder(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cart not found for user ID: 1");
    }
}
