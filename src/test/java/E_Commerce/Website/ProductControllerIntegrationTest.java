package E_Commerce.Website;

import E_Commerce.Website.dto.ProductDTO;
import E_Commerce.Website.entity.Category;
import E_Commerce.Website.entity.Role;
import E_Commerce.Website.entity.User;
import E_Commerce.Website.support.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void adminCanCreateReadUpdateAndDeleteProduct() throws Exception {
        User admin = createUser(Role.ADMIN);
        Category category = createCategory();
        ProductDTO createRequest = new ProductDTO(null, category.getId(), "Laptop", "Gaming laptop", 1200.0, "https://example.com/laptop.png", 10);

        MvcResult createResult = mockMvc.perform(post("/api/products")
                        .header("Authorization", bearerTokenFor(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.categoryId").value(category.getId()))
                .andReturn();

        JsonNode createdProduct = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long productId = createdProduct.get("id").asLong();

        mockMvc.perform(get("/api/products/{id}", productId)
                        .header("Authorization", bearerTokenFor(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.categoryId").value(category.getId()));

        mockMvc.perform(put("/api/products/{id}", productId)
                        .header("Authorization", bearerTokenFor(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new ProductDTO(null, category.getId(), "Laptop Pro", "Updated laptop", 1500.0, "https://example.com/pro.png", 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Pro"))
                .andExpect(jsonPath("$.price").value(1500.0));

        mockMvc.perform(delete("/api/products/{id}", productId)
                        .header("Authorization", bearerTokenFor(admin)))
                .andExpect(status().isNoContent());

        assertThat(productRepository.findById(productId)).isEmpty();
    }

    @Test
    void customerCannotCreateProduct_returnsForbidden() throws Exception {
        User customer = createUser(Role.CUSTOMER);
        Category category = createCategory();

        mockMvc.perform(post("/api/products")
                        .header("Authorization", bearerTokenFor(customer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new ProductDTO(null, category.getId(), "Laptop", "Gaming laptop", 1200.0, null, 10))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"));
    }
}
