CREATE TABLE chat_message (
	id serial primary key;
	author varchar(20);
	message varchar(400);
	sending_date timestamp;
)
