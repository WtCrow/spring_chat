var CONNECTION_STATUS = 'connect'
var DISCONNECTION_STATUS = 'disconnect'
var COMMAND_UPDATE_USERS = 'update_users_list', COMMAND_ONLINE_USERS_LIST = 'online_users_list',
    COMMAND_NEW_MESSAGE = 'new_message', CLIENT_COMMAND_NEW_MESSAGE = 'new_message',
    CLIENT_COMMAND_SET_NICKNAME = 'set_nickname', COMMAND_ERROR = 'error';

var conn = null;
var nickname = '';
var users = [];

$(function () {
    $('#nickname_button').click(function() { connect(); });
    $('#send').click(function() { sendMessage(); });
    addEventListener('keydown', keyPress);
});

function keyPress(e)
{
    // if Enter press
	if(e.keyCode == 13) {
	    // if not nickname, "click" to #nickname_button button
	    if (nickname != '') {
	        sendMessage();
	    } else {
	        // else "click" to #send button
	        connect();
	    }
	}
}

function connect() {
    /*
    * Function for connect to ws server
    */
    if ($('#nickname_input').val() == '') return;

    // hide login items
    $('#nickname_input').hide();
    $('#nickname_button').hide();

    // enable client for sending messages
    nickname = $('#nickname_input').val();
    $('#nickname_label').removeAttr('hidden');
    $('#nickname_span').append('<b>Your nickname: ' + nickname + '</b>');
    $('#message').removeAttr('disabled');
    $('#send').removeAttr('disabled');

    // connect to WebSocket
    var wsUri = (window.location.protocol == 'https:' && 'wss://' || 'ws://') + window.location.host + '/chat';
    conn = new WebSocket(wsUri);
    conn.onmessage = function(e) {
        /*
        * Convert data to dict and analise command value
        */
        var message = JSON.parse(e.data);
        // console.log(message);

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
            case COMMAND_ERROR:
                alert(message['content']);
            break;
        }
    };
    conn.onopen = function() {
        /*
        * Send request for init user
        */
        conn.send(JSON.stringify({'command': CLIENT_COMMAND_SET_NICKNAME, 'content': nickname}));
    };
    conn.onclose = function() {
        conn = null;
    };
}

function sendMessage() {
    /*
    * Function for send message from input item #message
    */
    if ($('#message').val() == '') return;
    conn.send(JSON.stringify({'command': CLIENT_COMMAND_NEW_MESSAGE, 'content': $('#message').val()}));
    $('#message').val('');
}

function showMessage(message) {
    /*
    * Function for add new message from chat
    */
    // if scroll in bottom, then scroll
    var isNeedScroll =  $('#messages')[0].scrollHeight - $('#messages')[0].offsetHeight - $('#messages')[0].scrollTop < 1

    $('#messages').append('<p><b>' + message.author + ':</b> ' + message.message + '</p>');

    if (isNeedScroll) {
        $('#messages')[0].scrollTop = $('#messages')[0].scrollHeight;
    }
}

function setOnlineUsers() {
    /*
    * Function for update list online users
    */
    users.sort();
    usersHtml = ''
    for (var i = 0; i < users.length; i++) {
        usersHtml += '<tr><td>' + users[i] + '</td></tr>';
    }
    $('#users').html(usersHtml);
}
