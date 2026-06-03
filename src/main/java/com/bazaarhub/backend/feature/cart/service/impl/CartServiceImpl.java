package com.bazaarhub.backend.feature.cart.service.impl;

import com.bazaarhub.backend.feature.cart.entity.Cart;
import com.bazaarhub.backend.feature.cart.entity.CartItem;
import com.bazaarhub.backend.feature.cart.exception.CartItemNotFoundException;
import com.bazaarhub.backend.feature.cart.exception.CartNotFoundException;
import com.bazaarhub.backend.feature.cart.resource.response.CartItemResponseDto;
import com.bazaarhub.backend.shared.enums.CartItemStatus;
import com.bazaarhub.backend.shared.enums.ProductStatus;
import com.bazaarhub.backend.shared.exception.InsufficientStockException;
import com.bazaarhub.backend.feature.cart.mapper.CartMapper;
import com.bazaarhub.backend.feature.cart.repository.CartRepository;
import com.bazaarhub.backend.feature.cart.resource.request.CartItemRequestDto;
import com.bazaarhub.backend.feature.cart.resource.response.CartResponseDto;
import com.bazaarhub.backend.feature.cart.service.CartService;
import com.bazaarhub.backend.feature.product.entity.Product;
import com.bazaarhub.backend.feature.product.exception.ProductNotFoundException;
import com.bazaarhub.backend.feature.product.repository.ProductRepository;
import com.bazaarhub.backend.feature.user.entity.User;
import com.bazaarhub.backend.feature.user.exception.UserNotFoundException;
import com.bazaarhub.backend.feature.user.repository.UserRepository;
import com.bazaarhub.backend.shared.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponseDto addToCart(Long userId, CartItemRequestDto cartItemRequestDto) {
        User user = userRepository.findByIdAndUserStatusNot(userId, UserStatus.DELETED).orElseThrow(() -> {
            log.error("User not found of id: {}", userId);
            return new UserNotFoundException("User not found.");
        });
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return newCart;
        });
        Product product = productRepository.findById(cartItemRequestDto.getProductId()).orElseThrow(() -> {
            log.error("Product of id: {}", cartItemRequestDto.getProductId());
            return new ProductNotFoundException("Product not found.");
        });

        //check if product already exists in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId())).findFirst().orElse(null);

        if (existingItem != null) {
            //update quantity
            int newQuantity = existingItem.getQuantity() + cartItemRequestDto.getQuantity();
            if (newQuantity > product.getStockQuantity()) {
                log.error("Requested quantity exceeds available stock.");
                throw new InsufficientStockException("Requested quantity exceeds available stock.");
            }
            existingItem.setQuantity(newQuantity);
            existingItem.setPricePerUnit(product.getPrice());
        } else {
            //create new cart item
            if (cartItemRequestDto.getQuantity() > product.getStockQuantity()) {
                log.error("Requested quantity exceeds available stock.");
                throw new InsufficientStockException("Requested quantity exceeds available stock.");
            }
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(cartItemRequestDto.getQuantity());
            newItem.setPricePerUnit(product.getPrice());

            cart.addItem(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return buildCartResponse(savedCart);
    }

    @Override
    public CartResponseDto updateCartItem(Long userId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("Cart not found of id: {}", userId);
            return new CartNotFoundException("Cart not found");
        });
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Item not found in cart.");
                    return new CartItemNotFoundException("Item not found  in cart.");
                });

        Product product = item.getProduct();

        if (quantity > product.getStockQuantity()) {
            log.error("Requested quantity exceeds available stock.");
            throw new InsufficientStockException("Requested quantity exceeds available stock.");
        }

        item.setQuantity(quantity);
        Cart savedCart = cartRepository.save(cart);
        return buildCartResponse(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDto getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("Cart not found of id: {}", userId);
            return new CartNotFoundException("Cart not found.");
        });
        return buildCartResponse(cart);
    }

    @Override
    public CartResponseDto removeItemFromCart(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("Cart not found of id: {}", userId);
            return new CartNotFoundException("Cart not found.");
        });
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Item not found in cart.");
                    return new CartItemNotFoundException("Item not found in cart.");
                });
        cart.removeItem(item);
        Cart savedCart = cartRepository.save(cart);
        return buildCartResponse(savedCart);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> {
            log.error("Cart not found of id: {}", userId);
            return new CartNotFoundException("Cart not found.");
        });
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartItemStatus determineStatus(CartItem item) {
        Product product = item.getProduct();

        if (product.getStatus() != ProductStatus.ACTIVE) {
            return CartItemStatus.PRODUCT_INACTIVE;
        }

        if (product.getStockQuantity() == null || product.getStockQuantity() <= 0) {
            return CartItemStatus.OUT_OF_STOCK;
        }

        if (product.getStockQuantity() < item.getQuantity()) {
            return CartItemStatus.LIMITED_STOCK;
        }
        return CartItemStatus.AVAILABLE;
    }

    private CartResponseDto buildCartResponse(Cart cart) {
        List<CartItemResponseDto> items = cart.getItems().stream()
                .map(item -> cartMapper.mapToCartItemResponse(item, determineStatus(item)))
                .toList();

        return cartMapper.mapToCartResponse(cart, items);
    }
}
