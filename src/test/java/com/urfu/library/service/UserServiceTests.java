package com.urfu.library.service;

import com.urfu.library.model.Role;
import com.urfu.library.model.User;
import com.urfu.library.model.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.NameAlreadyBoundException;
import java.util.Optional;

/**
 * Тесты методов сервиса для работы с сущностью User
 * @author Alexandr FIlatov
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    /**
     * Настройка перед каждым тестом, создание тестовых пользователей с закодированными паролями
     */
    @BeforeEach
    public void setUp() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user = new User(1L,"Vanya","123@gmail.com",
                passwordEncoder.encode("qwerty"), Role.ROLE_USER);
    }

    /**
     * Тестирует успешное создание нового пользователя в системе
     * @author Alexandr FIlatov
     */
    @Test
    public void testCreateUser_Success() throws NameAlreadyBoundException {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        Assertions.assertEquals(user, userService.createUser(user));

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    /**
     * Тестирует безуспешное создание пользователя в системе,
     * т.к. пользователь с таким логином уже зарегистрирован
     * @author Alexandr FIlatov
     */
    @Test
    public void testCreateUser_Failure() {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        Exception exception = Assertions.assertThrows(NameAlreadyBoundException.class, () -> userService.createUser(user));
        Assertions.assertEquals("Username Vanya already taken", exception.getMessage());
    }

    /**
     * Тестирует успешное получение пользователя по логину
     * @author Alexandr FIlatov
     */
    @Test
    public void testLoadUserByUsername_Success() {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        UserDetails foundUser = userService.loadUserByUsername(user.getUsername());

        Assertions.assertEquals(user, foundUser);
    }

    /**
     * Тестирует безуспешное получение пользователя по логину, т.к. его нет в системе
     * @author Alexandr FIlatov
     */
    @Test
    public void testLoadUserByUsername_Failure() {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(user.getUsername()));
        Assertions.assertEquals("Username not found: Vanya", exception.getMessage());
    }

    /**
     * Тестирует проверку доступности имени пользователя
     */
    @Test
    public void testIsUserExist() {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        Assertions.assertTrue(userService.isUserExist(user.getUsername(), "test"));

        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        Assertions.assertFalse(userService.isUserExist(user.getUsername(), "test"));
    }
}
