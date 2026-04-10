package com.bazaarhub.backend.config;

import lombok.AllArgsConstructor;
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
    public static final String category_CACHE_NAME = "categories";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("user", "vendor", "customerProfile", "categories");
    }
}
