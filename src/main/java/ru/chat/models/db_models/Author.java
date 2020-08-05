package ru.chat.models.db_models;

import javax.persistence.*;


@Entity
@Table(name = "chat_author")
public class Author {
    /*
     * Model author message for hibernate
     * */

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int author;

    @Column(name="name", length = 30, unique = true)
    private String name;

    public Author(String name) {
        this.name = name;
    }

    public Author() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public String toString() {
        return this.name;
    }
}
