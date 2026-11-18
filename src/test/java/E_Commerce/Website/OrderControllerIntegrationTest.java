package E_Commerce.Website;

import E_Commerce.Website.entity.Cart;
import E_Commerce.Website.entity.Category;
import E_Commerce.Website.entity.Product;
import E_Commerce.Website.entity.Role;
import E_Commerce.Website.entity.User;
import E_Commerce.Website.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void customerCanPlaceOrder_whenCartHasItems() throws Exception {
        User customer = createUser(Role.CUSTOMER);
        Category category = createCategory();
        Product product = createProduct(category);
        Cart cart = createCart(customer);
        addCartItem(cart, product, 2);

        mockMvc.perform(post("/api/orders/place/{userId}", customer.getId())
                        .header("Authorization", bearerTokenFor(customer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order placed successfully "));

        assertThat(orderRepository.count()).isEqualTo(1);
        assertThat(cartItemRepository.count()).isZero();
        assertThat(cartRepository.findByUserId(customer.getId()).orElseThrow().getCartItems()).isEmpty();
    }

    @Test
    void customerCannotPlaceOrder_whenCartIsEmpty() throws Exception {
        User customer = createUser(Role.CUSTOMER);
        createCart(customer);

        mockMvc.perform(post("/api/orders/place/{userId}", customer.getId())
                        .header("Authorization", bearerTokenFor(customer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cart is empty. No items to order."));
    }
}
