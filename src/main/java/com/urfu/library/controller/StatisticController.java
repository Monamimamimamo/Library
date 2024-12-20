package com.urfu.library.controller;

import com.urfu.library.controller.dto.StatisticDto;
import com.urfu.library.model.Role;
import com.urfu.library.model.Statistic;
import com.urfu.library.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private final StatisticService statisticService;

    @Autowired
    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    /**
     * Обработка запросов на получение статистики (своей или по имени/почте пользователя)
     */
    @GetMapping
    public ResponseEntity<StatisticDto> getStatistic(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Statistic> statisticOptional;
        if (authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
            if (username != null) {
                statisticOptional = statisticService.getStatisticByUsername(username);
            } else if (email != null) {
                statisticOptional = statisticService.getStatisticByEmail(email);
            } else {
                statisticOptional = statisticService.getStatisticByUsername(authentication.getName());
            }
        } else if (username != null || email != null) {
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
