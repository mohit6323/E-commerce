package E_Commerce.Website.services.impl;

import E_Commerce.Website.entity.*;
import E_Commerce.Website.dto.OrderDTO;
import E_Commerce.Website.dto.OrderItemDTO;
import E_Commerce.Website.exception.ResourceNotFoundException;
import E_Commerce.Website.repository.*;
import E_Commerce.Website.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    public String placeOrder(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(()
                -> new ResourceNotFoundException("Cart not found for user ID: " + userId));
        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. No items to order.");
        }
        // Validate stock for all items first
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product.getStock() == null || product.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
        }
        //Create Order
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus("Pending");
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        double totalAmount=0;

        //Save Order First
        Order savedOrder = orderRepository.save(order);

        //Convert CartItems Into OrderItems and update stock
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            // Deduct stock
            int newStock = product.getStock() - cartItem.getQuantity();
            product.setStock(newStock);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            totalAmount += cartItem.getPrice() * cartItem.getQuantity();
            orderItemRepository.save(orderItem);
        }
        //Update Order Amount
        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        //Clear Cart
        cartItemRepository.deleteAll(cartItems);
        cart.getCartItems().clear();
        cartRepository.save(cart);
        log.info("Placed order {} for user {} with total {}", savedOrder.getId(), userId, totalAmount);

        return "Order placed successfully ";
    }

    @Override
    public String placeOrderByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));
        return placeOrder(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByUserEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return getOrdersByUserId(user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::toOrderDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status.name());
        Order updated = orderRepository.save(order);
        return toOrderDto(updated);
    }

    private OrderDTO toOrderDto(Order order) {
        List<OrderItemDTO> orderItems = order.getOrderItems() == null
                ? new ArrayList<>()
                : order.getOrderItems().stream()
                .map(this::toOrderItemDto)
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getUser().getId(),
                orderItems
        );
    }

    private OrderItemDTO toOrderItemDto(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }
}
