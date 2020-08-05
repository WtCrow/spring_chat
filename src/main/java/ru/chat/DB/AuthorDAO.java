package ru.chat.DB;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.chat.models.db_models.Author;

import java.util.List;


public class AuthorDAO {
    /*
     * DAO for Author class
     *
     * --- CRUD incomplete
     * */

    public static Author getOrCreateAuthor(String name) {
        /*
        * Return Author object with name == name
        *
        * Create if not exist
        *
        * */

        Session session = SessionFactoryUtil.getInstance().openSession();
        Transaction transaction = session.beginTransaction();

        List authorList = null;
        authorList = session.createQuery("FROM Author a WHERE a.name = :name")
                .setParameter("name", name)
                .list();

        Author author = null;
        if (authorList.size() == 0) {
            author = new Author(name);
            session.save(author);
        }
        else {
            author = (Author) authorList.get(0);  // name authors unique, also list contain one or zero authors
        }

        transaction.commit();
        session.close();
        return author;
    }
}
