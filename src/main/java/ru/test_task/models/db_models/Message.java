package ru.test_task.models.db_models;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "chat_message")
public class Message {
    /*
     * Model message for hibernate
     * */

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(name="message", length = 140)
    private String message;

    @Column(name="sending_date")
    private LocalDateTime sending_date;

    public Message(Author author, String message) {
        this.author = author;
        this.message = message;
        this.sending_date = LocalDateTime.now();
    }

    public Message() {}

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getSending_date() {
        return sending_date;
    }

    public void setSending_date(LocalDateTime sending_date) {
        this.sending_date = sending_date;
    }

    public String toString() {
        return this.author + ": " + this.message;
    }

}
