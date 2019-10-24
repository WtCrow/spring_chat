package ru.test_task.models;

public class ServerMessage {
    /*
    * Model for message from server for client
    * */

    private String command;
    private Object content;

    public void setCommand(String command) {
        this.command = command;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getCommand() {
        return this.command;
    }

    public Object getContent() {
        return this.content;
    }

    public ServerMessage(String command, Object content) {
        this.command = command;
        this.content = content;
    }
}
