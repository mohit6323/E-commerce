package E_Commerce.Website.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String orderNumber;
    private Double totalAmount;
    private String status;
    private Long userId;
    private List<OrderItemDTO> orderItems = new ArrayList<>();
}
