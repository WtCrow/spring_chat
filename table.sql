CREATE TABLE chat_message (
	id serial primary key;
	author varchar(20);
	message varchar(100);
	sending_date timestamp;
)
