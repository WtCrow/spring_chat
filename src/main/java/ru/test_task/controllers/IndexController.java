package ru.test_task.controllers;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class IndexController {
    /*
    * Return index page
    * */

    @RequestMapping("/")
    @ResponseBody
    public String welcome() {
        return "index";
    }
}
