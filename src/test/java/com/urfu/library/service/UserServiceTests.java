package com.urfu.library.service;

import com.urfu.library.controller.dto.UserRequestDto;
import com.urfu.library.model.Role;
import com.urfu.library.model.User;
import com.urfu.library.model.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * Тесты методов сервиса для работы с сущностью User
 * @author Alexandr FIlatov
 */
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    private User admin;

    private UserRequestDto userRequestDto;

    private UserRequestDto adminRequestDto;
    /**
     * Настройка перед каждым тестом, создание тестовых пользователей с закодированными паролями
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userRequestDto = new UserRequestDto("Vanya", "123@gmai.com", "qwerty");
        adminRequestDto = new UserRequestDto("Petya", "1234@gmail.com", "qwerty");
        user = new User(1L,"Vanya","123@gmail.com",
                passwordEncoder.encode("qwerty"), Role.ROLE_USER);
        admin = new User(1L,"Petya","1234@gmail.com",
                passwordEncoder.encode("qwerty"), Role.ROLE_ADMIN);
    }

    /**
     * Тестирует успешное создание нового пользователя в системе
     * @author Alexandr FIlatov
     */
    @Test
    public void createUser_Success() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        boolean flag = userService.createUser(userRequestDto);
        Optional<User> createdUser = userRepository.findById(user.getId());

        Assertions.assertTrue(flag);
        Assertions.assertTrue(createdUser.isPresent());
        Assertions.assertEquals(user, createdUser.get());
    }

    /**
     * Тестирует безуспешное создание пользователя в системе,
     * т.к. пользователь с таким логином уже зарегистрирован
     * @author Alexandr FIlatov
     */
    @Test
    public void createUser_Failure() {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        userService.createUser(userRequestDto);
        boolean flag = userService.createUser(userRequestDto);
        Optional<User> createdUser = userRepository.findById(user.getId());

        Assertions.assertFalse(flag);
        Assertions.assertFalse(createdUser.isPresent());
    }

    /**
     * Тестирует успешное создание пользователя-админа в системе
     * @author Alexandr FIlatov
     */
    @Test
    public void createAdmin_Success() {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        boolean flag = userService.createAdmin(userRequestDto);
        Optional<User> createdAdmin = userRepository.findById(admin.getId());

        Assertions.assertTrue(flag);
        Assertions.assertTrue(createdAdmin.isPresent());
        Assertions.assertEquals(user, createdAdmin.get());
    }

    /**
     * Тестирует безуспешное создание пользователя-админа в системе,
     * т.к. пользователь с таким логином уже зарегистрирован
     * @author Alexandr FIlatov
     */
    @Test
    public void createAdmin_Failure() {
        Mockito.when(userRepository.findByUsername(admin.getUsername())).thenReturn(admin);
        userService.createAdmin(adminRequestDto);
        boolean flag = userService.createAdmin(adminRequestDto);
        Optional<User> createdAdmin = userRepository.findById(admin.getId());

        Assertions.assertFalse(flag);
        Assertions.assertFalse(createdAdmin.isPresent());
    }

    /**
     * Тестирует успешное получение пользователя по логину
     * @author Alexandr FIlatov
     */
    @Test
    public void loadUserByUsername_Success() {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        UserDetails foundUser = userService.loadUserByUsername(user.getUsername());

        Assertions.assertEquals(user, foundUser);
    }

    /**
     * Тестирует безуспешное получение пользователя по логину, т.к. его нет в системе
     * @author Alexandr FIlatov
     */
    @Test
    public void loadUserByUsername_Failure() {
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(null);

        UsernameNotFoundException exception = Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(user.getUsername()));
        Assertions.assertEquals("Username not found: Vanya", exception.getMessage());
    }
}
