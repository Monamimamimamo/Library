package com.urfu.library.controller;

import com.urfu.library.controller.dto.StatisticDto;
import com.urfu.library.model.Role;
import com.urfu.library.model.Statistic;
import com.urfu.library.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

/**
 * Класс для обработки запросов связанных со статистикой
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticController {
    private StatisticService statisticService;

    @Autowired
    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    /**
     * Обработка запросов на получение статистики (своей или по имени пользователя)
     */
    @GetMapping
    public ResponseEntity<StatisticDto> getStatistic(Authentication authentication, String username, String email) {
        Optional<Statistic> statisticOptional;
        if (username != null && authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
            statisticOptional = statisticService.getStatisticByUsername(username);
        } else if (email != null && authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
            statisticOptional = statisticService.getStatisticByEmail(email);
        } else if((username != null || email != null) && !authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            statisticOptional = statisticService.getStatisticByUsername(authentication.getName());
        }

        if (statisticOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        Statistic statistic = statisticOptional.get();
        Period period = Period.between(statistic.getRegistrationDate().toLocalDate(), LocalDate.now());
        String existedFor = String.format("%d years, %d months, %d days",
                period.getYears(), period.getMonths(), period.getDays());

        StatisticDto statisticDto = new StatisticDto(statistic.getInTimeReturned().toString(),
                statistic.getLateReturned().toString(), existedFor);

        return new ResponseEntity<>(statisticDto, HttpStatus.OK);
    }
}