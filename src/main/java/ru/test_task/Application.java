package ru.test_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.test_task.DB.SessionFactoryUtil;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // init hibernate
        SessionFactoryUtil.getInstance();
        // start app
        SpringApplication.run(Application.class, args);
    }

}
