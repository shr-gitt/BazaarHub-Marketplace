package com.bazaarhub.backend.feature.order.service.impl;

import com.bazaarhub.backend.feature.cart.entity.Cart;
import com.bazaarhub.backend.feature.cart.entity.CartItem;
import com.bazaarhub.backend.feature.cart.repository.CartRepository;
import com.bazaarhub.backend.feature.order.entity.Order;
import com.bazaarhub.backend.feature.order.entity.OrderItem;
import com.bazaarhub.backend.feature.order.mapper.OrderMapper;
import com.bazaarhub.backend.feature.order.repository.OrderRepository;
import com.bazaarhub.backend.feature.order.resources.request.OrderRequestDto;
import com.bazaarhub.backend.feature.order.resources.request.OrderStatusUpdateRequestDto;
import com.bazaarhub.backend.feature.order.resources.response.OrderResponseDto;
import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.shared.enums.*;

import com.bazaarhub.backend.shared.exception.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void placeOrder_success() {

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        Product product = new Product();
        product.setName("Laptop");
        product.setStockQuantity(10);
        product.setStatus(ProductStatus.ACTIVE);

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setPricePerUnit(BigDecimal.valueOf(100));

        List<CartItem> items = new ArrayList<>();
        items.add(item);

        Cart cart = new Cart();
        cart.setItems(items);

        OrderRequestDto request = new OrderRequestDto();
        request.setShippingAddress("Kathmandu");
        request.setContactNumber("9800000000");

        Order savedOrder = new Order();

        when(userRepository.findByIdAndUserStatusNot(eq(userId), any(UserStatus.class)))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        when(orderMapper.mapToOrderResponseDTO(any(Order.class)))
                .thenReturn(new OrderResponseDto());

        OrderResponseDto response = orderService.placeOrder(userId, request);

        assertNotNull(response);
        assertEquals(8, product.getStockQuantity()); // 10 - 2

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void placeOrder_userNotFound() {

        when(userRepository.findByIdAndUserStatusNot(anyLong(), any(UserStatus.class)))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> orderService.placeOrder(1L, new OrderRequestDto()));
    }

    @Test
    void placeOrder_emptyCart() {

        User user = new User();

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>()); // FIXED

        when(userRepository.findByIdAndUserStatusNot(anyLong(), any(UserStatus.class)))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserId(anyLong()))
                .thenReturn(Optional.of(cart));

        assertThrows(EmptyCartCheckoutException.class,
                () -> orderService.placeOrder(1L, new OrderRequestDto()));
    }

    @Test
    void placeOrder_insufficientStock() {

        Product product = new Product();
        product.setName("Phone");
        product.setStockQuantity(1);
        product.setStatus(ProductStatus.ACTIVE);

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(5);

        List<CartItem> items = new ArrayList<>();
        items.add(item);

        Cart cart = new Cart();
        cart.setItems(items);

        when(userRepository.findByIdAndUserStatusNot(anyLong(), any(UserStatus.class)))
                .thenReturn(Optional.of(new User()));

        when(cartRepository.findByUserId(anyLong()))
                .thenReturn(Optional.of(cart));

        assertThrows(InsufficientStockException.class,
                () -> orderService.placeOrder(1L, new OrderRequestDto()));
    }

    @Test
    void placeOrder_inactiveProduct() {

        Product product = new Product();
        product.setName("Tablet");
        product.setStatus(ProductStatus.INACTIVE);

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(1);

        List<CartItem> items = new ArrayList<>();
        items.add(item);

        Cart cart = new Cart();
        cart.setItems(items);

        when(userRepository.findByIdAndUserStatusNot(anyLong(), any(UserStatus.class)))
                .thenReturn(Optional.of(new User()));

        when(cartRepository.findByUserId(anyLong()))
                .thenReturn(Optional.of(cart));

        assertThrows(InvalidOrderStateException.class,
                () -> orderService.placeOrder(1L, new OrderRequestDto()));
    }

    @Test
    void cancelOrder_success() {

        Long userId = 1L;
        Long orderId = 10L;

        Product product = new Product();
        product.setStockQuantity(5);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        Order order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(item);
        order.setOrderItems(orderItems);

        when(orderRepository.findByIdAndUserId(orderId, userId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(orderMapper.mapToOrderResponseDTO(any(Order.class)))
                .thenReturn(new OrderResponseDto());

        OrderResponseDto response = orderService.cancelOrder(userId, orderId);

        assertNotNull(response);
        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertEquals(7, product.getStockQuantity());
    }

    @Test
    void cancelOrder_invalidState() {

        Order order = new Order();
        order.setOrderStatus(OrderStatus.DELIVERED);

        when(orderRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class,
                () -> orderService.cancelOrder(1L, 1L));
    }

    @Test
    void updateOrderStatus_success() {

        Order order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);

        OrderStatusUpdateRequestDto dto = new OrderStatusUpdateRequestDto();
        dto.setOrderStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(orderMapper.mapToOrderResponseDTO(any(Order.class)))
                .thenReturn(new OrderResponseDto());

        OrderResponseDto response = orderService.updateOrderStatus(1L, dto);

        assertNotNull(response);
        assertEquals(OrderStatus.SHIPPED, order.getOrderStatus());
    }
}