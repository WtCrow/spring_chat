package ru.test_task.models;


public class ClientMessage {
    /*
     * Model from client for server
     * */

    private String command;
    private String content;

    public ClientMessage() {}

    public ClientMessage(String command, String content) {
        this.command = command;
        this.content = content;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
