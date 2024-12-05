package com.urfu.library.controller;

import com.urfu.library.controller.advice.RestControllerAdvice;
import com.urfu.library.model.Statistic;
import com.urfu.library.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Тест на обработку запросов связанных со статистикой
 */
@ExtendWith(MockitoExtension.class)
public class StatisticControllerTests {
    @Mock
    private StatisticService statisticService;
    @InjectMocks
    private StatisticController statisticController;

    private Statistic statistic = new Statistic(1L, "alex", LocalDateTime.now().minusDays(45), 2L, 2L);

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(statisticController).setControllerAdvice(RestControllerAdvice.class).build();
    }

    /**
     * Тест на получение статистики по имени пользователя
     */
    @Test
    @WithMockUser(username = "alex", roles = {"ADMIN"})
    public void getStatisticByUsernameTest() throws Exception {
        Mockito.when(statisticService.getStatisticByUsername(Mockito.anyString())).thenReturn(Optional.of(statistic));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics?username=alex")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"lateReturned\": \"2\", \"inTimeReturned\": \"2\"," +
                                " \"existedFor\": \"0 years, 1 months, 15 days\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Тест на получение статистики, которой нет в системе по имени пользователя
     */
    @Test
    @WithMockUser(username = "alex", roles = {"ADMIN"})
    public void getStatisticByUsernameTest_NoContent() throws Exception {
        Mockito.when(statisticService.getStatisticByUsername(Mockito.anyString())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics?username=alex"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Тест на получение своей статистики
     */
    @Test
    @WithMockUser(username = "alex", roles = {"ADMIN"})
    public void getStatisticTest() throws Exception {
        Mockito.when(statisticService.getStatisticByUsername(Mockito.anyString())).thenReturn(Optional.of(statistic));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"lateReturned\": \"2\", \"inTimeReturned\": \"2\"," +
                                " \"existedFor\": \"0 years, 1 months, 15 days\" }"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Тест на получение своей статистики
     */
    @Test
    @WithMockUser(username = "alex", roles = {"ADMIN"})
    public void getStatisticTest_NoContent() throws Exception {
        Mockito.when(statisticService.getStatisticByUsername(Mockito.anyString())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/statistics"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
