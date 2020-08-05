package ru.chat.DB;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class SessionFactoryUtil {
    /*
    * Class for create session factory with DB
    * */

    private static SessionFactory instance;

    private SessionFactoryUtil() {}

    public static SessionFactory getInstance() {
        if (instance == null) {
            Configuration configuration = new Configuration().configure();

            configuration.setProperty("hibernate.connection.url", System.getenv("DB_URL"));
            configuration.setProperty("hibernate.connection.username", System.getenv("DB_USER"));
            configuration.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));

            instance = configuration.buildSessionFactory();
        }
        return instance;
    }
}
