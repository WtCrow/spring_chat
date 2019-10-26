package ru.test_task.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.test_task.DB.AuthorDAO;
import ru.test_task.DB.MessagesDAO;
import ru.test_task.custom_serialization.MessageSerializer;
import ru.test_task.models.ClientMessage;
import ru.test_task.models.ConnectNotification;
import ru.test_task.models.db_models.Author;
import ru.test_task.models.db_models.Message;
import ru.test_task.models.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class ChatSocketHandler extends TextWebSocketHandler {
    /*
    * Controller for WebSocket from /chat endpoint
    * */

    // Set custom serializer for Message
    private Gson gson = new GsonBuilder().registerTypeAdapter(Message.class, new MessageSerializer()).create();

    // All connected sessions
    private Map<WebSocketSession, Author> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {}

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            // Close if need
            if (!status.equals(CloseStatus.NORMAL)) {
                session.close();
            }
        } catch (IOException e) {}

        // push notification about disconnect
        String disconnectNickname = this.sessions.get(session).getName();
        ConnectNotification disconnect = new ConnectNotification(disconnectNickname, ConnectNotification.STATUS_DISCONNECTION);
        ServerMessage serverMessage = new ServerMessage(ServerMessage.COMMAND_UPDATE_USERS, disconnect);
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
        /*
        * New user message
        * */
        ClientMessage clientMessageMessage = null;
        try {
            clientMessageMessage = this.gson.fromJson(message.getPayload(), ClientMessage.class);
        } catch (JsonParseException e) {
            String errorJson = this.gson.toJson(
                    new ServerMessage(ServerMessage.COMMAND_ERROR, ServerMessage.ERROR_BAD_MESSAGE));
            try {
                session.sendMessage(new TextMessage(errorJson));
            } catch (IOException ioE) {}
            return;
        }

        // Check client command
        switch (clientMessageMessage.getCommand()) {
            case ClientMessage.CLIENT_COMMAND_SET_NICKNAME:
                this.initNewUser(session, clientMessageMessage.getContent());
                break;
            case ClientMessage.CLIENT_COMMAND_NEW_MESSAGE:
                this.newMessage(session, clientMessageMessage.getContent());
                break;
        }
    }

    private void initNewUser(WebSocketSession session, String nickname) {
        /*
        * Send notification about new user, send new user last 10 message and list online users
        * */
        if (nickname.length() > 30 || nickname.length() == 0) {
            String errorJson = this.gson.toJson(
                    new ServerMessage(ServerMessage.COMMAND_ERROR, ServerMessage.ERROR_LENGTH_NICKNAME));
            try {
                session.sendMessage(new TextMessage(errorJson));
            } catch (IOException e) {}
            return;
        }

        // new user notification
        ConnectNotification connectNotification = new ConnectNotification(nickname, ConnectNotification.STATUS_CONNECTION);
        ServerMessage serverMessage = new ServerMessage(ServerMessage.COMMAND_UPDATE_USERS, connectNotification);
        this.sendAllUsers(serverMessage);

        // get Author or create
        Author author = AuthorDAO.createAuthor(nickname);
        this.sessions.put(session, author);

        // send last 20 messages
        List<Message> messages = MessagesDAO.readMessages(20);
        for (Message msg: messages) {
            ServerMessage newMessage = new ServerMessage(ServerMessage.COMMAND_NEW_MESSAGE, msg);
            String jsonMessages = this.gson.toJson(newMessage);
            try {
                session.sendMessage(new TextMessage(jsonMessages));
            } catch (IOException e) {}
        }

        // send array active users
        ArrayList<String> nicknames = new ArrayList<>();
        for (Author onlineUser: this.sessions.values()) {
            nicknames.add(onlineUser.getName());
        }
        ServerMessage usersList = new ServerMessage(ServerMessage.COMMAND_ONLINE_USERS_LIST, nicknames);
        String jsonUsers = this.gson.toJson(usersList);
        try {
            session.sendMessage(new TextMessage(jsonUsers));
        } catch (IOException e) {}
    }

    private void newMessage(WebSocketSession session, String message) {
        /*
        * Save message and send all users
        * */
        if (!this.sessions.containsKey(session)) {
            String errorJson = this.gson.toJson(
                    new ServerMessage(ServerMessage.COMMAND_ERROR, ServerMessage.ERROR_NOT_LOGIN_USER));
            try {
                session.sendMessage(new TextMessage(errorJson));
            } catch (IOException e) {}
            return;
        }

        if (message.length() > 140 || message.length() == 0) {
            String errorJson = this.gson.toJson(
                    new ServerMessage(ServerMessage.COMMAND_ERROR, ServerMessage.ERROR_LENGTH_MESSAGE));
            try {
                session.sendMessage(new TextMessage(errorJson));
            } catch (IOException e) {}
            return;
        }

        // protected of XSS
        Map<String, String> symbols = new HashMap<>();
        symbols.put("&", "&amp;");
        symbols.put("<", "&lt;");
        symbols.put(">", "&gt;");
        for (Map.Entry<String, String> symbol : symbols.entrySet()) {
            message = message.replace(symbol.getKey(), symbol.getValue());
        }

        // save message to DB
        Author author = this.sessions.get(session);
        Message newMessage = new Message(author, message);
        MessagesDAO.createMessage(newMessage);

        // send new message all users
        ServerMessage serverMessage = new ServerMessage(ServerMessage.COMMAND_NEW_MESSAGE, newMessage);
        this.sendAllUsers(serverMessage);
    }

    private void sendAllUsers(ServerMessage message) {
        /*
        * Send ServerMessage all online users
        * */

        String json = this.gson.toJson(message);

        for(Map.Entry<WebSocketSession, Author> entry : this.sessions.entrySet()) {
            WebSocketSession user = entry.getKey();
            try {
                user.sendMessage(new TextMessage(json));
            } catch (IOException e) {}
        }
    }
}
