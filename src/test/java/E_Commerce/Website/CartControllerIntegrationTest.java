package E_Commerce.Website;

import E_Commerce.Website.dto.CartResponseDTO;
import E_Commerce.Website.entity.Category;
import E_Commerce.Website.entity.Product;
import E_Commerce.Website.entity.Role;
import E_Commerce.Website.entity.User;
import E_Commerce.Website.support.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CartControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void customerCanAddViewAndRemoveCartItems() throws Exception {
        User customer = createUser(Role.CUSTOMER);
        Category category = createCategory();
        Product product = createProduct(category);

        mockMvc.perform(post("/api/carts/add/{userId}", customer.getId())
                        .header("Authorization", bearerTokenFor(customer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new CartResponseDTO(product.getId(), 2))))
                .andExpect(status().isOk())
                .andExpect(content().string("Product added to cart successfully"));

        MvcResult cartResult = mockMvc.perform(get("/api/carts/{userId}", customer.getId())
                        .header("Authorization", bearerTokenFor(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(customer.getId()))
                .andExpect(jsonPath("$.cartItems[0].productId").value(product.getId()))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(2))
                .andExpect(jsonPath("$.totalAmount").value(399.98))
                .andReturn();

        JsonNode cart = objectMapper.readTree(cartResult.getResponse().getContentAsString());
        long cartItemId = cart.get("cartItems").get(0).get("id").asLong();

        mockMvc.perform(delete("/api/carts/remove/{cartItemId}", cartItemId)
                        .header("Authorization", bearerTokenFor(customer)))
                .andExpect(status().isOk())
                .andExpect(content().string("Product removed from cart successfully"));

        mockMvc.perform(get("/api/carts/{userId}", customer.getId())
                        .header("Authorization", bearerTokenFor(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems").isEmpty());
    }

    @Test
    void adminCannotAccessCustomerCart_returnsForbidden() throws Exception {
        User admin = createUser(Role.ADMIN);
        User customer = createUser(Role.CUSTOMER);

        mockMvc.perform(get("/api/carts/{userId}", customer.getId())
                        .header("Authorization", bearerTokenFor(admin)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"));
    }

    @Test
    void addToCart_withInvalidQuantity_returnsValidationError() throws Exception {
        User customer = createUser(Role.CUSTOMER);
        Category category = createCategory();
        Product product = createProduct(category);

        mockMvc.perform(post("/api/carts/add/{userId}", customer.getId())
                        .header("Authorization", bearerTokenFor(customer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new CartResponseDTO(product.getId(), 0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.quantity").value("Quantity must be at least 1"));
    }
}
