package ru.test_task.DB;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.test_task.models.Message;


public class MessagesDAO {
    /*
    * Simple DAO for Message class
    * */

    public static List<Message> getMessages(int count) {
        Session sessionObj = null;
        sessionObj = SessionFactoryUtil.getSessionFactory().openSession();
        sessionObj.beginTransaction();
        List messagesList = sessionObj.createQuery("FROM Message m ORDER BY m.sending_date DESC").setMaxResults(count).list();
        ArrayList<Message> messages = new ArrayList<Message>();
        for (Object msg: messagesList) {
            messages.add(0, ((Message)msg));
        }
        sessionObj.close();
        return messages;
    }

    public static void saveMessage(Message message) {
        Session session = SessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(message);
        tx1.commit();
        session.close();
    }
}


