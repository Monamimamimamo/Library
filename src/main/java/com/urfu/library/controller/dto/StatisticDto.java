package com.urfu.library.controller.dto;

/**
 * DTO для статистки
 * @param inTimeReturned вовремя возвращенные книги
 * @param lateReturned книги, возвращенные с нарушением дедлайна
 * @param existedFor как давно пользователь зарегистрирован в системе
 */
public record StatisticDto(String inTimeReturned, String lateReturned, String existedFor) {
}
