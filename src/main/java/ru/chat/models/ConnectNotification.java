package ru.chat.models;


public class ConnectNotification {
    /*
     * Model for notification about connect/disconnect user
     * */
    public static final String STATUS_CONNECTION = "connect";
    public static final String STATUS_DISCONNECTION = "disconnect";

    private String nickname;
    private String status;

    public ConnectNotification(String nickname, String status) {
        this.nickname = nickname;
        this.status = status;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
