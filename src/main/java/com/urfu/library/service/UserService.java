package com.urfu.library.service;

import com.urfu.library.model.Statistic;
import com.urfu.library.model.User;
import com.urfu.library.model.repository.StatisticRepository;
import com.urfu.library.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.NameAlreadyBoundException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Сервис для работы с сущностью User.
 * Предоставляет методы для создания и получения пользователей.
 * @author Alexandr FIlatov
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StatisticRepository statisticRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, StatisticRepository statisticRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.statisticRepository = statisticRepository;
        this.encoder = encoder;
    }

    /**
     * Создание нового пользователя в системе
     * @param user пользователь
     * @return user - созданный пользователь
     * @throws NameAlreadyBoundException если имя пользователя уже занято
     */
    public User createUser(User user) throws NameAlreadyBoundException {
        if (!isUserExist(user.getUsername(), user.getEmail())) {
            throw new NameAlreadyBoundException("Username or email already taken");
        }
        Statistic statistic = new Statistic(user.getUsername(), LocalDateTime.now(), 0L, 0L);
        statisticRepository.save(statistic);
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
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
    public boolean isUserExist(String username, String email) {
        if(userRepository.findByUsername(username).isPresent()) {
            return false;
        }
        return userRepository.findByEmail(email).isEmpty();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
        return user.get();
    }
}
