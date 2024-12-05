package com.urfu.library.service;

import com.urfu.library.model.User;
import com.urfu.library.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.NameAlreadyBoundException;
import java.util.Optional;

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
     * @param user пользователь
     * @return user - созданный пользователь
     * @throws NameAlreadyBoundException если имя пользователя уже занято
     */
    public User createUser(User user) throws NameAlreadyBoundException {
        if (!isUserExist(user.getUsername())) {
            throw new NameAlreadyBoundException("Username " + user.getUsername() + " already taken");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return repository.save(user);
    }

    /**
     * Проверяет свободно ли имя пользователя
     * @param username имя для проверки
     * @return
     * <ul>
     *     <li>true - имя пользователя свободно</li>
     *     <li>false - имя пользователя занято</li>
     * </ul>
     */
    public boolean isUserExist(String username) {
        if(repository.findByUsername(username).isPresent()) {
            return false;
        }
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
        return user.get();
    }
}
