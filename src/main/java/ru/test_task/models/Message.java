package ru.test_task.models;

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

    @Column(name="author")
    private String author;

    @Column(name="message")
    private String message;

    @Column(name="sending_date")
    private LocalDateTime sending_date;

    public Message(String author, String message) {
        this.author = author;
        this.message = message;
        this.sending_date = LocalDateTime.now();
    }

    public Message() {}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
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
