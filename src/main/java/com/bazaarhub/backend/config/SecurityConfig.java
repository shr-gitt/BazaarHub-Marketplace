package com.bazaarhub.backend.config;

import com.bazaarhub.backend.feature.auth.security.CustomUserDetailsService;
import com.bazaarhub.backend.feature.auth.security.JwtAuthFilter;
import com.bazaarhub.backend.shared.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authenticationProvider(daoAuthenticationProvider()).authorizeHttpRequests(request -> request.requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name()).requestMatchers(HttpMethod.GET, "/api/category/**").hasAnyRole(Role.ADMIN.name(),Role.VENDOR.name(),Role.CUSTOMER.name()).requestMatchers(HttpMethod.POST, "/api/category/**").hasAnyRole(Role.ADMIN.name(),Role.VENDOR.name()).requestMatchers(HttpMethod.PUT, "/api/category/**").hasAnyRole(Role.ADMIN.name(),Role.VENDOR.name()).requestMatchers(HttpMethod.DELETE, "/api/category/**").hasAnyRole(Role.ADMIN.name(),Role.VENDOR.name())).sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).userDetailsService(customUserDetailsService).authorizeHttpRequests(auth -> auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api/register", "/api/login").permitAll().anyRequest().authenticated())

                .httpBasic(Customizer.withDefaults()).addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }


}
