package com.bazaarhub.backend.feature.order.service.impl;

import com.bazaarhub.backend.config.CacheConfig;
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
import com.bazaarhub.backend.feature.order.service.OrderService;
import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.shared.enums.OrderStatus;
import com.bazaarhub.backend.shared.enums.PaymentStatus;
import com.bazaarhub.backend.shared.enums.ProductStatus;
import com.bazaarhub.backend.shared.enums.UserStatus;
import com.bazaarhub.backend.shared.exception.EmptyCartCheckoutException;
import com.bazaarhub.backend.shared.exception.InsufficientStockException;
import com.bazaarhub.backend.shared.exception.InvalidOrderStateException;
import com.bazaarhub.backend.shared.exception.OrderNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;


    @Override
    @Transactional
    @CachePut(cacheNames = CacheConfig.CREATE_ORDER_CACHE, key = "#userId")
    public OrderResponseDto placeOrder(Long userId, OrderRequestDto orderRequestDTO) {
        User user = userRepository.findByIdAndUserStatusNot(userId, UserStatus.DELETED).orElseThrow(() -> {
            log.error("User not found of id : {}", userId);
            return new UserNotFoundException("User not found");
        });

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("Cart not found of user id : {}", userId);
            return new EmptyCartCheckoutException("Cart not found");
        });

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartCheckoutException("Cannot place order from an empty cart.");
        }
        validateCartItems(cart);

        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShippingAddress(orderRequestDTO.getShippingAddress());
        order.setContactNumber(orderRequestDTO.getContactNumber());
        order.setRemarks(orderRequestDTO.getRemark());
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            BigDecimal unitPrice = cartItem.getPricePerUnit();
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPricePerUnit(unitPrice);
            orderItem.setTotalPrice(totalPrice);
            order.addOrderItem(orderItem);
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            totalAmount = totalAmount.add(totalPrice);
        }
        order.setTotalAmount((totalAmount));
        Order saveOrder = orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);
        return orderMapper.mapToOrderResponseDTO(saveOrder);

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.GET_ORDER_CACHE, key = "#orderId")
    public OrderResponseDto getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(() -> {
            log.error("Order not found of id: {}", orderId);
            return new OrderNotFoundException("Order Not Found Exception");
        });
        return orderMapper.mapToOrderResponseDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.GET_ORDER_CACHE, key = "#userId")
    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(orderMapper::mapToOrderResponseDTO).toList();
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CacheConfig.UPDATE_ORDER_CACHE, key = "#orderId")
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateRequestDto requestDTO) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("Order not found of id: {}", orderId);
            return new OrderNotFoundException("Order Not Found Exception");
        });
        validateOrderStatusTransition(order.getOrderStatus(), requestDTO.getOrderStatus());
        order.setOrderStatus(requestDTO.getOrderStatus());
        Order saveOrder = orderRepository.save(order);
        return orderMapper.mapToOrderResponseDTO(saveOrder);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = CacheConfig.CANCEL_ORDER_CACHE, key = "#orderId")
    public OrderResponseDto cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            log.error("Order not found of id: {}", orderId);
            return new OrderNotFoundException("Order Not Found Exception");
        });
        if (order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Order cannot be cancelled after shipping or delivery.");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Order is already cancelled.");
        }
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.mapToOrderResponseDTO(savedOrder);

    }

    private void validateCartItems(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();

            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new InvalidOrderStateException("Product '" + product.getName() + "' is inactive and cannot be ordered.");
            }

            if (product.getStockQuantity() == null || product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }
        }
    }

    private void validateOrderStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Order status cannot be changed from " + currentStatus);
        }
    }
}
