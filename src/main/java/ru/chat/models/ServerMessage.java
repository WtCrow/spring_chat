package ru.chat.models;


public class ServerMessage {
    /*
    * Model for message from server for client
    *
    * command contain constant string
    * object contain data this command
    *
    * */
    public static final String COMMAND_UPDATE_USERS = "update_users_list";
    public static final String COMMAND_ONLINE_USERS_LIST = "online_users_list";
    public static final String COMMAND_NEW_MESSAGE = "new_message";

    public static final String COMMAND_ERROR = "error";

    public static final String ERROR_LENGTH_NICKNAME = "nickname max length = 30, min length = 1";
    public static final String ERROR_LENGTH_MESSAGE = "message max length = 140, min length = 1";
    public static final String ERROR_NOT_LOGIN_USER = "for sending message need authorized";
    public static final String ERROR_BAD_MESSAGE = "bad message format";

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
