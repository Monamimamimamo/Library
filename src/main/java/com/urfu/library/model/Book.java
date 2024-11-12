package com.urfu.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

/**
 * Сущность книги
 */
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Title must be not null")
    private String title;
    @NotBlank(message = "Author must be not null")
    private String author;
    @NotBlank(message = "Description must be not null")
    private String description;

    private boolean isReserved;

    /**
     * Конструктор с явным указанием всех значений для полей сущности
     */
    public Book(Long id, String title, String author, String description, boolean isReserved) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.isReserved = isReserved;
    }

    /**
     * Конструктор с указанием значений для всех полей, кроме ID - генерируется автоматически
     */
    public Book(String title, String author, String description, boolean isReserved) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.isReserved = isReserved;
    }

    public Book(){
        super();
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
