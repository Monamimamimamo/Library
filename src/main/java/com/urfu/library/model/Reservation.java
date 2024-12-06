package com.urfu.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Сущность бронирования
 */
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long bookId;

    private Long userId;

    private boolean isReturned;

    private boolean isDeadlineMissed;

    private LocalDateTime startDate;

    private LocalDateTime finishDate;

    /**
     * Конструктор с указанием значений для всех полей, кроме ID - генерируется автоматически
     */
    public Reservation(Long bookId, Long userId, boolean isReturned, boolean isDeadlineMissed, LocalDateTime startDate, LocalDateTime finishDate) {
        this.bookId = bookId;
        this.userId = userId;
        this.isReturned = isReturned;
        this.isDeadlineMissed = isDeadlineMissed;
        this.startDate = startDate;
        this.finishDate = finishDate;
    }

    public Reservation(){
        super();
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public boolean isDeadlineMissed() {
        return isDeadlineMissed;
    }

    public void setDeadlineMissed(boolean deadlineMissed) {
        isDeadlineMissed = deadlineMissed;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }
}
