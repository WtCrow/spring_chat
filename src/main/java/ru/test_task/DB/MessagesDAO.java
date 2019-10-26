package ru.test_task.DB;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.test_task.models.db_models.Message;


public class MessagesDAO {
    /*
    * DAO for Message class
    *
    * --- CRUD incomplete
    * */

    public static List<Message> readMessages(int count) {
        /*
         * Return list contain count last added messages
         * */
        Session session = SessionFactoryUtil.getInstance().openSession();
        Transaction transaction = session.beginTransaction();

        List messagesList = session
                .createQuery("FROM Message m ORDER BY m.sending_date DESC")
                .setMaxResults(count)
                .list();

        ArrayList<Message> messages = new ArrayList<>();
        for (Object msg: messagesList) {
            messages.add(0, ((Message)msg));
        }

        transaction.commit();
        session.close();
        return messages;
    }

    public static Message createMessage(Message message) {
        /*
        * Save object Message and return this object
        * */
        Session session = SessionFactoryUtil.getInstance().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(message);
        transaction.commit();
        session.close();
        return message;
    }
}
