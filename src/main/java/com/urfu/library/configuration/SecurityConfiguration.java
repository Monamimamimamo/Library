package com.urfu.library.configuration;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Конфигурация Spring Security
 * @author Alexandr FIlatov
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Создает кодировщик паролей
     * @author Alexandr FIlatov
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создает цепочку фильтров и настраивает форму логина
     * Цепочка фильтров: нет ограничений на регистрацию, для остальных запросов нужно быть авторизованными
     * Логин: после signIn пользователь считается авторизованным
     * @author Alexandr FIlatov
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((request) -> request
                        .requestMatchers(HttpMethod.POST, "/api/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/signup").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/book").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/book/{bookId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/book/{bookId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/book/reservation/return/{bookId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/book/reservation/all").hasRole("ADMIN")
                        .anyRequest().authenticated())

                .formLogin(form -> form.loginPage("/api/signIn")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        }))
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler(new AccessDeniedHandlerImpl())
                );

        return http.build();
    }
}