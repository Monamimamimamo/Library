package com.urfu.library.configuration;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создает цепочку фильтров и настраивает форму логина
     * @author Alexandr FIlatov
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((request) -> request
                        .requestMatchers(HttpMethod.POST, "/api/admin/signup").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/book").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/book/{bookId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/book/{bookId}").hasRole("ADMIN")
                        .anyRequest().permitAll())

                .formLogin(form -> form.loginPage("/api/signIn")
                        .successHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        }));

        return http.build();
    }
}