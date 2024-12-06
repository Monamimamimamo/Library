package com.urfu.library.configuration;

import com.urfu.library.model.Role;
import com.urfu.library.model.User;
import com.urfu.library.model.Book;
import com.urfu.library.model.repository.BookRepository;
import com.urfu.library.model.repository.UserRepository;
import com.urfu.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Интеграционные тесты цепочки фильтров
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class SecurityFilterChainTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    @Qualifier("userRepository")
    private UserRepository userRepository;

    @Autowired
    @Qualifier("bookRepository")
    private BookRepository bookRepository;

    @Autowired
    private UserService userService;

    /**
     * Настройка перед каждым сервисом, очистка тестовой БД и инициализация mockMvc
     */
    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        bookRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     * Тест неограниченного доступа к регистрации пользователей
     */
    @Test
    public void testPublicAccessToSignup() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"alex\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    /**
     * Тест на доступ к регистрации администраторов для администратора
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminSignUpAccess_Allowed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"alex\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    /**
     * Тест на отказ в доступе к регистрации администраторов для обычного пользователя
     */
    @Test
    @WithMockUser(roles = "USER")
    public void testAdminSignUpAccess_Denied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"alex\", \"password\": \"qwerty\", \"email\": \"123@gmail.com\" }"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Тест на доступ к созданию книги для администратора
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateBook_Allowed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Test Title\", \"author\": \"Test Author\", \"description\": \"Test Description\" }"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    /**
     * Тест на отказ в доступе к созданию книги для обычного пользователя
     */
    @Test
    @WithMockUser(roles = "USER")
    public void testCreateBook_Denied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/book").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Test Title\", \"author\": \"Test Author\", \"description\": \"Test Description\" }"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Тест на доступ к редактуре информации о книге для администратора
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateBookInfo_Allowed() throws Exception {
        bookRepository.save(new Book("test", "test", "test",true));
        long bookId = bookRepository.findByTitle("test").getFirst().getId();
        mockMvc.perform(MockMvcRequestBuilders.put("/api/book/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Updated Title\", \"author\": \"Updated Author\", \"description\": \"Updated Description\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Тест на отказ в доступе к редактуре книги для обычного пользователя
     */
    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateBookInfo_Denied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/book/{bookId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"Updated Title\", \"author\": \"Updated Author\", \"description\": \"Updated Description\" }"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Тест на доступ к удалению книг для администратора
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteBook_Allowed() throws Exception {
        Book book = new Book("test", "test", "test",false);
        bookRepository.save(book);
        long bookId = bookRepository.findByTitle("test").getFirst().getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/book/{bookId}", bookId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Тест на отказ в доступе к удалению книг для обычного пользователя
     */
    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteBook_Denied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/book/{bookId}", 1L))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Тест на доступ к подтверждению возврата книги для администратора
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testReturnBook_Allowed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/book/reservation/return/{bookId}", 1L))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Тест на отказ в доступе к подтверждению возврата книги для обычного пользователя
     */
    @Test
    @WithMockUser(roles = "USER")
    public void testReturnBook_Denied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/book/reservation/return/{bookId}", 1L))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Тест на доступ к получению списка всех резерваций для администратора
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllActiveReservations_Allowed() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/reservation/all"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Тест на отказ в доступе на получение списка всех резерваций для обычного пользователя
     */
    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllActiveReservations_Denied() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/book/reservation/all"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Тест на успешный вход в систему
     */
    @Test
    public void signIn_Success() throws Exception {
        User user = new User("alex", "123@gmail.com", "qwerty", Role.ROLE_USER);
        userService.createUser(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signIn?username=alex&password=qwerty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Тест на не успешный вход в систему (неверный пароль)
     */
    @Test
    public void signIn_Failure() throws Exception {
        User user = new User("alex", "123@gmail.com", "qwerty", Role.ROLE_USER);
        userService.createUser(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/signIn?username=Alex&password=qwerty"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}

