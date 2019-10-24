var CONNECTION_STATUS = 'connect'
var DISCONNECTION_STATUS = 'disconnect'
var COMMAND_UPDATE_USERS = "update_users_list", COMMAND_ONLINE_USERS_LIST = "online_users_list",
    COMMAND_NEW_MESSAGE = "new_message", CLIENT_COMMAND_NEW_MESSAGE = "new_message",
    CLIENT_COMMAND_SET_NICKNAME = "set_nickname";

var conn = null;
var nickname = '';
var users = [];

$(function () {
    $( "#nickname_button" ).click(function() { connect(); });
    $( "#send" ).click(function() { sendMessage(); });
});

function connect() {
    $("#nickname_input").hide();
    $("#nickname_button").hide();

    nickname = $("#nickname_input").val();
    $("#nickname_label").removeAttr('hidden');
    $("#nickname_span").append("Your nickname: " + nickname);
    $("#message").removeAttr('disabled');
    $("#send").removeAttr('disabled');

    var wsUri = (window.location.protocol == 'https:' && 'wss://' || 'ws://') + window.location.host + '/chat';
    conn = new WebSocket(wsUri);
    conn.onmessage = function(e) {
        var message = JSON.parse(e.data);
        console.log(message);

        switch (message['command']) {
            case COMMAND_ONLINE_USERS_LIST:
                users = message['content'];
                setOnlineUsers();
            break;
            case COMMAND_UPDATE_USERS:
                if (message['content']['status'] == CONNECTION_STATUS) {
                    users.push(message['content']['nickname']);
                } else {
                    var index = users.indexOf(message['content']['nickname']);
                    if (index > -1) {
                      users.splice(index, 1);
                    }
                }
                setOnlineUsers();
            break;
            case COMMAND_NEW_MESSAGE:
                showMessage(message['content']);
            break;
        }
    };
    conn.onopen = function() {
        conn.send(JSON.stringify({'command': CLIENT_COMMAND_SET_NICKNAME, 'content': nickname}));
    };
    conn.onclose = function() {
//        alert('Connection to server is close')
        conn = null;
    };
}

function sendMessage() {
    conn.send(JSON.stringify({'command': CLIENT_COMMAND_NEW_MESSAGE, 'content': $("#message").val()}));
    $("#message").val('');
}

function showMessage(message) {
    $("#messages").append("<p>" + message.author + ': ' + message.message + "</p>");
}

function setOnlineUsers() {
    users.sort();
    usersHtml = ''
    for (var i = 0; i < users.length; i++) {
        usersHtml += "<tr><td>" + users[i] + "</td></tr>";
    }
    $("#users").html(usersHtml);
}
