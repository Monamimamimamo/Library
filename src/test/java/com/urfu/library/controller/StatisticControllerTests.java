package com.urfu.library.controller;

import com.urfu.library.controller.advice.RestControllerAdvice;
import com.urfu.library.model.Role;
import com.urfu.library.model.Statistic;
import com.urfu.library.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Тест на обработку запросов связанных со статистикой
 */
@ExtendWith(MockitoExtension.class)
public class StatisticControllerTests {

    @Mock
    private StatisticService statisticService;

    @Mock
    SecurityContext securityContext;

    @InjectMocks
    private StatisticController statisticController;

    private final Statistic statistic = new Statistic(1L, "alex",
            LocalDateTime.now().minusDays(45), 2L, 2L);

    Authentication authentication = new TestingAuthenticationToken(null,
            null, List.of(Role.ROLE_ADMIN));

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
        mockMvc = MockMvcBuilders.standaloneSetup(statisticController)
                .setControllerAdvice(RestControllerAdvice.class).build();
    }

    /**
     * Тест на получение статистики пользователя с заданным логином
     */
    @Test
    @WithMockUser(username = "alex", roles = {"ROLE_ADMIN"})
    public void getStatisticByUsernameTest() throws Exception {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(statisticService.getStatisticByUsername(Mockito.anyString()))
                .thenReturn(Optional.of(statistic));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics?username=alex"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lateReturned").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.inTimeReturned").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.existedFor")
                        .value("0 years, 1 months, 15 days"));
    }

    /**
     * Тест на получение статистики пользователя с заданной почтой
     */
    @Test
    @WithMockUser(username = "alex", roles = {"ADMIN"})
    public void getStatisticByEmailTest() throws Exception {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(statisticService.getStatisticByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(statistic));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics?email=123@mail.ru"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lateReturned").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.inTimeReturned").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.existedFor")
                        .value("0 years, 1 months, 15 days"));
    }

    /**
     * Тест на получение своей статистики
     */
    @Test
    @WithMockUser(username = "alex", roles = {"ADMIN"})
    public void getMyStatisticTest() throws Exception {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(statisticService.getStatisticByUsername(Mockito.anyString()))
                .thenReturn(Optional.of(statistic));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lateReturned").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.inTimeReturned").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.existedFor")
                        .value("0 years, 1 months, 15 days"));
    }

    /**
     * Тест на получение статистики, которой нет в системе
     */
    @Test
    @WithMockUser(username = "alex", roles = {"ADMIN"})
    public void getStatisticTest_NoContent() throws Exception {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(statisticService.getStatisticByUsername(Mockito.anyString()))
                .thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics?username=alex"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Тест на отсутствие доступа к получению статистики по имени пользователя для роли USER
     */
    @Test
    @WithMockUser(username = "alex")
    public void getStatisticByUsernameTest_Forbidden() throws Exception {
        Authentication userAuthentication = new TestingAuthenticationToken(null,
                null, List.of(Role.ROLE_USER));
        Mockito.when(securityContext.getAuthentication()).thenReturn(userAuthentication);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics?username=alex"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Тест на отсутствие доступа к получению статистики по почте пользователя для роли USER
     */
    @Test
    @WithMockUser(username = "alex")
    public void getStatisticByEmailTest_Forbidden() throws Exception {
        Authentication userAuthentication = new TestingAuthenticationToken(null,
                null, List.of(Role.ROLE_USER));
        Mockito.when(securityContext.getAuthentication()).thenReturn(userAuthentication);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics?email=123@gmail.com"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
