package com.bazaarhub.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {
    public static final String USER_CACHE_NAME = "user";
    public static final String VENDOR_CACHE_NAME = "vendor";
    public static final String CUSTOMER_CACHE_NAME = "customerProfile";
    public static final String CATEGORY_CACHE_NAME = "category";
    public static final String PRODUCT_CACHE_NAME = "product";
    public static final String CART_CACHE_NAME = "cart";
    public static final String CREATE_ORDER_CACHE = "create_order_cache";
    public static final String GET_ORDER_CACHE = "get_order_cache";
    public static final String UPDATE_ORDER_CACHE = "update_order_cache";
    public static final String CANCEL_ORDER_CACHE = "cancel_order_cache";
    public static final String ADDRESS_CACHE_NAME = "address";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "user",
                "vendor",
                "customerProfile",
                "product",
                "category",
                "cart",
                "create_order_cache",
                "get_order_cache",
                "update_order_cache",
                "cancel_order_cache",
                "address"
        );
    }
}
