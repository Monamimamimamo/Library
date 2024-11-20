package com.urfu.library.controller.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Dto запросов для сущности User
 * @param username - имя пользователя
 * @param email - почта пользователя
 * @param password - пароль пользователя
 */
public record UserRequestDto(@NotBlank String username,
                             @NotBlank String email,
                             @NotBlank String password) {
}
