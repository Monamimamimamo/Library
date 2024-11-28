package com.urfu.library.controller;

import com.urfu.library.controller.advice.RestControllerAdvice;
import com.urfu.library.model.Role;
import com.urfu.library.model.User;
import com.urfu.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.naming.NameAlreadyBoundException;

/**
 * Тесты контроллера для обработки запросов связанных с сущностью User
 * @author Alexandr FIlatov
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private User user;

    /**
     * Настройка перед каждым тестом, инициализация mockMvc,
     * получение кодировщика паролей и создание тестового пользователя
     */
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(RestControllerAdvice.class).build();
        user = new User("Vanya","123@gmail.com", "qwerty", Role.ROLE_USER);
    }

    /**
     * Тест успешного создания нового пользователя в системе
     * @author Alexandr FIlatov
     */
    @Test
    public void testCreateUser_Success() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"Vanya\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(userService, Mockito.times(1)).createUser(ArgumentMatchers
                .argThat(argument -> argument.equals(user) && argument.getRole().equals(Role.ROLE_USER)));
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
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenThrow(NameAlreadyBoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"Vanya\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    /**
     * Тест успешного создания нового администратора в системе
     * @author Alexandr FIlatov
     */
    @Test
    public void testCreateAdmin_Success() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"Vanya\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(userService, Mockito.times(1)).createUser(ArgumentMatchers
                .argThat(argument -> argument.equals(user) && argument.getRole().equals(Role.ROLE_ADMIN)));
    }

    /**
     * Тест безуспешного создания нового администратора в системе при вводе невалидных данных
     * @author Alexandr FIlatov
     */
    @Test
    public void testCreateAdmin_UnprocessableEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

        Mockito.verify(userService, Mockito.never()).createUser(ArgumentMatchers.any(User.class));
    }

    /**
     * Тестирует безуспешное добавление нового администратора в систему в случае,
     * если указанный логин уже содержится в базе данных
     * @author Alexandr FIlatov
     */
    @Test
    public void testCreateAdmin_AlreadyExist() throws Exception {
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenThrow(NameAlreadyBoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"Vanya\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}
