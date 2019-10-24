package ru.test_task.controllers;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.test_task.DB.MessagesDAO;
import ru.test_task.models.ClientMessage;
import ru.test_task.models.Connection;
import ru.test_task.models.Message;
import ru.test_task.models.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChatSocketHandler extends TextWebSocketHandler {
    private static final String STATUS_CONNECTION = "connect";
    private static final String STATUS_DISCONNECTION = "disconnect";

    private static final String COMMAND_UPDATE_USERS = "update_users_list";
    private static final String COMMAND_ONLINE_USERS_LIST = "online_users_list";
    private static final String COMMAND_NEW_MESSAGE = "new_message";

    private static final String CLIENT_COMMAND_NEW_MESSAGE = "new_message";
    private static final String CLIENT_COMMAND_SET_NICKNAME = "set_nickname";

    private static final String COMMAND_ERROR = "error";

    private static final String ERROR_MAX_LENGTH_NICKNAME = "max length nickname = 20";
    private static final String ERROR_MAX_LENGTH_MESSAGE = "max length message = 100";

    // All connected sessions
    private Map<WebSocketSession, String> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {}

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Check status
        try {
            if (!status.equals(CloseStatus.NORMAL)) {
                session.close();
            }
        } catch (IOException e) {}

        // push notification about disconnect
        String disconnectNickname = this.sessions.get(session);
        Connection disconnect = new Connection(disconnectNickname, ChatSocketHandler.STATUS_DISCONNECTION);
        ServerMessage serverMessage = new ServerMessage(ChatSocketHandler.COMMAND_UPDATE_USERS, disconnect);
        this.sessions.remove(session);
        this.sendAllUsers(serverMessage);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        try {
            session.close();
        } catch (IOException e) {
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Gson gson = new Gson();
        ClientMessage clientMessageMessage = gson.fromJson(message.getPayload(), ClientMessage.class);

        // Check client command
        switch (clientMessageMessage.getCommand()) {
            case ChatSocketHandler.CLIENT_COMMAND_SET_NICKNAME:
                this.initNewUser(session, clientMessageMessage.getContent());
                break;
            case ChatSocketHandler.CLIENT_COMMAND_NEW_MESSAGE:
                this.sendMessage(session, clientMessageMessage.getContent());
                break;
        }
    }

    private void initNewUser(WebSocketSession session, String nickname) {
        /*
        * Send notification about new user, send new user last 10 message and list online users
        * */
        Gson gson = new Gson();

        if (nickname.length() > 20) {
            String errorJson = gson.toJson(
                    new ServerMessage(ChatSocketHandler.COMMAND_ERROR, ChatSocketHandler.ERROR_MAX_LENGTH_NICKNAME));
            try {
                session.sendMessage(new TextMessage(errorJson));
                session.close();
            } catch (IOException e) {}
            return;
        }

        Connection connection = new Connection(nickname, ChatSocketHandler.STATUS_CONNECTION);
        ServerMessage serverMessage = new ServerMessage(ChatSocketHandler.COMMAND_UPDATE_USERS, connection);
        this.sendAllUsers(serverMessage);

        this.sessions.put(session, nickname);

        List<Message> messages = MessagesDAO.getMessages(10);
        for (Message msg: messages) {
            ServerMessage newMessage = new ServerMessage(ChatSocketHandler.COMMAND_NEW_MESSAGE, msg);
            String jsonMessages = gson.toJson(newMessage);
            try {
                session.sendMessage(new TextMessage(jsonMessages));
            } catch (IOException e) {}
        }

        ServerMessage usersList = new ServerMessage(ChatSocketHandler.COMMAND_ONLINE_USERS_LIST, this.sessions.values());
        String jsonUsers = gson.toJson(usersList);
        try {
            session.sendMessage(new TextMessage(jsonUsers));
        } catch (IOException e) {}
    }

    private void sendMessage(WebSocketSession session, String message) {
        /*
        * Save message and send all users
        * */
        if (message.length() == 0) { return; }

        Gson gson = new Gson();
        if (message.length() > 100) {
            String errorJson = gson.toJson(
                    new ServerMessage(ChatSocketHandler.COMMAND_ERROR, ChatSocketHandler.ERROR_MAX_LENGTH_MESSAGE));
            try {
                session.sendMessage(new TextMessage(errorJson));
            } catch (IOException e) {}
            return;
        }

        Map<String, String> symbols = new HashMap<>();
        symbols.put("&", "&amp;");
        symbols.put("<", "&lt;");
        symbols.put(">", "&gt;");

        for (Map.Entry<String, String> symbol : symbols.entrySet()) {
            message = message.replace(symbol.getKey(), symbol.getValue());
        }

        Message newMessage = new Message(this.sessions.get(session), message);
        MessagesDAO.saveMessage(newMessage);
        ServerMessage serverMessage = new ServerMessage(ChatSocketHandler.COMMAND_NEW_MESSAGE, newMessage);
        this.sendAllUsers(serverMessage);
    }

    private void sendAllUsers(ServerMessage message) {
        /*
        * Send ServerMessage all online users
        * */

        Gson gson = new Gson();
        String json = gson.toJson(message);

        for(Map.Entry<WebSocketSession, String> entry : this.sessions.entrySet()) {
            WebSocketSession user = entry.getKey();
            try {
                user.sendMessage(new TextMessage(json));
            } catch (IOException e) {}
        }
    }
}
