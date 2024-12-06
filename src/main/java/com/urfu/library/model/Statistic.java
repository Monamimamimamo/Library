package com.urfu.library.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * Сущность для хранения статистики пользователя
 */
@Entity
public class Statistic {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String username;

    @Column
    private LocalDateTime registrationDate;

    @Column
    private Long lateReturned;

    @Column
    private Long inTimeReturned;

    public Statistic(Long id, String username, LocalDateTime registrationDate, Long lateReturned, Long inTimeReturned) {
        this.id = id;
        this.username = username;
        this.registrationDate = registrationDate;
        this.lateReturned = lateReturned;
        this.inTimeReturned = inTimeReturned;
    }

    public Statistic(String username, LocalDateTime registrationDate, Long lateReturned, Long inTimeReturned) {
        this(null, username, registrationDate, lateReturned, inTimeReturned);
    }

    public Statistic() {
        super();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime existedFor) {
        this.registrationDate = existedFor;
    }

    public Long getLateReturned() {
        return lateReturned;
    }

    public void setLateReturned(Long lateReturned) {
        this.lateReturned = lateReturned;
    }

    public Long getInTimeReturned() {
        return inTimeReturned;
    }

    public void setInTimeReturned(Long inTimeReturned) {
        this.inTimeReturned = inTimeReturned;
    }
}
