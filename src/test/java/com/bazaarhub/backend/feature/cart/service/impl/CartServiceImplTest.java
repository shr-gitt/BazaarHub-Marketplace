package com.bazaarhub.backend.feature.cart.service.impl;

import com.bazaarhub.backend.feature.cart.entity.Cart;
import com.bazaarhub.backend.feature.cart.entity.CartItem;
import com.bazaarhub.backend.feature.cart.mapper.CartMapper;
import com.bazaarhub.backend.feature.cart.repository.CartRepository;
import com.bazaarhub.backend.feature.cart.resource.request.CartItemRequestDto;
import com.bazaarhub.backend.feature.cart.resource.response.CartItemResponseDto;
import com.bazaarhub.backend.feature.cart.resource.response.CartResponseDto;
import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.product.exception.ProductNotFoundException;
import com.bazaarhub.backend.feature.product.repository.ProductRepository;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.shared.enums.CartItemStatus;
import com.bazaarhub.backend.shared.enums.ProductStatus;
import com.bazaarhub.backend.shared.enums.Role;
import com.bazaarhub.backend.shared.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private CartResponseDto cartResponseDto;
    private CartItemRequestDto cartItemRequestDto;
    private User user;
    private Cart cart;
    private CartItem cartItem;
    private Product product;
    private CartItemResponseDto cartItemResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setRole(Role.CUSTOMER);
        user.setUserStatus(UserStatus.ACTIVE);

        product = new Product();
        product.setId(10L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStockQuantity(10);
        product.setStatus(ProductStatus.ACTIVE);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPricePerUnit(BigDecimal.valueOf(100));
        cartItem.setTotalPrice(BigDecimal.valueOf(200));

        cartItemRequestDto = new CartItemRequestDto();
        cartItemRequestDto.setProductId(10L);
        cartItemRequestDto.setQuantity(2);

        cartItemResponseDto = new CartItemResponseDto(
                1L,
                10L,
                "Test Product",
                "imageUrl",
                2,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200),
                CartItemStatus.AVAILABLE
        );

        cartResponseDto = new CartResponseDto(
                1L,
                1L,
                List.of(cartItemResponseDto),
                BigDecimal.valueOf(200),
                null,
                null
        );
    }

    @Test
    void addToCart_shouldCreateCartAndAddNewItem() {
        when(userRepository.findByIdAndUserStatusNot(1L, UserStatus.DELETED)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(cartMapper.mapToCartItemResponse(any(CartItem.class), eq(CartItemStatus.AVAILABLE))).thenReturn(cartItemResponseDto);

        when(cartMapper.mapToCartResponse(any(Cart.class), anyList()))
                .thenReturn(cartResponseDto);
        CartResponseDto response = cartService.addToCart(1L, cartItemRequestDto);

        assertNotNull(response);
        verify(userRepository).findByIdAndUserStatusNot(1L, UserStatus.DELETED);
        verify(cartRepository).findByUserId(1L);
        verify(productRepository).findById(10L);
        verify(cartRepository).save(any(Cart.class));
        verify(cartMapper).mapToCartItemResponse(any(CartItem.class), eq(CartItemStatus.AVAILABLE));
        verify(cartMapper).mapToCartResponse(any(Cart.class), anyList());
    }


    @Test
    void addToCart_shouldIncreaseQuantityIfProductAlreadyExists() {
        cart.getItems().add(cartItem);

        when(userRepository.findByIdAndUserStatusNot(1L, UserStatus.DELETED)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CartItemResponseDto updatedItemResponse = new CartItemResponseDto(
                1L,
                10L,
                "Test Product",
                "imageUrl",
                4,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(400),
                CartItemStatus.AVAILABLE
        );
        when(cartMapper.mapToCartItemResponse(any(CartItem.class), eq(CartItemStatus.AVAILABLE)))
                .thenReturn(updatedItemResponse);
        when(cartMapper.mapToCartResponse(any(Cart.class), anyList()))
                .thenReturn(cartResponseDto);
        cartService.addToCart(1L, cartItemRequestDto);

        assertEquals(4, cartItem.getQuantity());
        verify(cartRepository).save(cart);
        verify(cartMapper).mapToCartItemResponse(any(CartItem.class), eq(CartItemStatus.AVAILABLE));
        verify(cartMapper).mapToCartResponse(any(Cart.class), anyList());

    }

    @Test
    void addToCart_shouldThrowWhenUserNotFound() {
        when(userRepository.findByIdAndUserStatusNot(1L, UserStatus.DELETED)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cartService.addToCart(1L, cartItemRequestDto));

        verify(userRepository).findByIdAndUserStatusNot(1L, UserStatus.DELETED);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addToCart_shouldThrowWhenProductNotFound() {
        when(userRepository.findByIdAndUserStatusNot(1L, UserStatus.DELETED)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> cartService.addToCart(1L, cartItemRequestDto));

        verify(productRepository).findById(10L);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void updateCartItem_shouldUpdateQuantity() {
        cart.getItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CartItemResponseDto updatedItemResponse = new CartItemResponseDto(
                1L,
                10L,
                "Test Product",
                "imageUrl",
                5,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(500),
                CartItemStatus.AVAILABLE
        );
        when(cartMapper.mapToCartItemResponse(any(CartItem.class), eq(CartItemStatus.AVAILABLE)))
                .thenReturn(updatedItemResponse);
        when(cartMapper.mapToCartResponse(any(Cart.class), anyList()))
                .thenReturn(cartResponseDto);

        CartResponseDto response = cartService.updateCartItem(1L, 10L, 5);

        assertNotNull(response);
        assertEquals(5, cartItem.getQuantity());
        verify(cartRepository).save(cart);
        verify(cartMapper).mapToCartItemResponse(any(CartItem.class), eq(CartItemStatus.AVAILABLE));
        verify(cartMapper).mapToCartResponse(any(Cart.class), anyList());
    }

    @Test
    void getCartByUserId_shouldReturnCart() {
        cart.getItems().add(cartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        when(cartMapper.mapToCartItemResponse(any(CartItem.class), eq(CartItemStatus.AVAILABLE)))
                .thenReturn(cartItemResponseDto);
        when(cartMapper.mapToCartResponse(any(Cart.class), anyList()))
                .thenReturn(cartResponseDto);

        CartResponseDto response = cartService.getCartByUserId(1L);

        assertNotNull(response);
        verify(cartRepository).findByUserId(1L);
        verify(cartMapper).mapToCartItemResponse(any(CartItem.class), eq(CartItemStatus.AVAILABLE));
        verify(cartMapper).mapToCartResponse(any(Cart.class), anyList());
    }

    @Test
    void removeItemFromCart_shouldRemoveItem() {
        cart.getItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartMapper.mapToCartResponse(any(Cart.class), anyList()))
                .thenReturn(cartResponseDto);
        CartResponseDto response = cartService.removeItemFromCart(1L, 10L);

        assertNotNull(response);
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
        verify(cartMapper).mapToCartResponse(any(Cart.class), anyList());

    }

    @Test
    void clearCart_shouldClearAllItems() {
        cart.getItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cartService.clearCart(1L);

        assertTrue(cart.getItems().isEmpty());
        verify(cartRepository).save(cart);
    }

}