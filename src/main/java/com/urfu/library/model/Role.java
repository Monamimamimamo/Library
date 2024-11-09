package com.urfu.library.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Роли пользователей в системе
 * @author Alexandr FIlatov
 */
public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
