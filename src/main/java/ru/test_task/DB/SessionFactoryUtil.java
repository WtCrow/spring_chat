package ru.test_task.DB;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.test_task.models.db_models.Message;


public class SessionFactoryUtil {
    /*
    * Class for create session factory with DB
    * */

    private static SessionFactory instance;

    private SessionFactoryUtil() {}

    public static SessionFactory getInstance() {
        if (instance == null) {
            Configuration configuration = new Configuration().configure();
            configuration.addAnnotatedClass(Message.class);
            instance = configuration.buildSessionFactory();
        }
        return instance;
    }
}
