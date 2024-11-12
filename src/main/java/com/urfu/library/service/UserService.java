package com.urfu.library.service;

import com.urfu.library.model.Role;
import com.urfu.library.model.User;
import com.urfu.library.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с сущностью User.
 * Предоставляет методы для создания и получения пользователей.
 * @author Alexandr FIlatov
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    /**
     * Создание нового пользователя в системе
     * @author Alexandr FIlatov
     */
    public boolean createUser(User user) {
        User checkUser = repository.findByUsername(user.getUsername());
        if (checkUser != null) {
            return false;
        }
        user.setRole(Role.ROLE_USER);
        user.setPassword(encoder.encode(user.getPassword()));
        repository.save(user);
        return true;
    }

    /**
     * Создание аккаунта для администратора
     * @author Alexandr FIlatov
     */
    public boolean createAdmin(User user) {
        User checkUser = repository.findByUsername(user.getUsername());
        if (checkUser != null) {
            return false;
        }
        user.setRole(Role.ROLE_ADMIN);
        user.setPassword(encoder.encode(user.getPassword()));
        repository.save(user);
        return true;
    }

    /**
     * Получение пользователя по логину
     * @author Alexandr FIlatov
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
        return user;
    }
}