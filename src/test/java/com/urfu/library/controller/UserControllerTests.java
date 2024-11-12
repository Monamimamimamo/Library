package com.urfu.library.controller;

import com.urfu.library.controller.advice.BookControllerAdvice;
import com.urfu.library.model.Role;
import com.urfu.library.model.User;
import com.urfu.library.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Тесты контроллера для обработки запросов связанных с сущностью User
 * @author Alexandr FIlatov
 */
public class UserControllerTests {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private User user;
    private User admin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(BookControllerAdvice.class).build();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user = new User(1L,"Vanya","123@gmail.com",
                passwordEncoder.encode("qwerty"), Role.ROLE_USER);
        admin = new User(2L,"Petya","1234@gmail.com",
                passwordEncoder.encode("qwerty"), Role.ROLE_ADMIN);
    }

    /**
     * Тест успешного создания нового пользователя в системе
     * @author Alexandr FIlatov
     */
    @Test
    public void testCreateUser_Success() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"Vanya\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(userService, Mockito.times(1)).createUser(user);
    }

    /**
     * Тест безуспешного создания нового пользователя в системе при вводе невалидных данных
     * @author Alexandr FIlatov
     */
    @Test
    public void testCreateUser_UnprocessableEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        Mockito.verify(userService, Mockito.never()).createUser(ArgumentMatchers.any(User.class));
    }

    /**
     * Тестирует безуспешное добавление нового пользователя в систему в случае,
     * если указанный логин уже содержится в базе данных
     * @author Alexandr FIlatov
     */
    @Test
    public void testCreateUser_AlreadyExist() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(false);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"Vanya\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        UserDetails createdUser = userService.loadUserByUsername(user.getUsername());

        Assertions.assertNull(createdUser);
    }
}