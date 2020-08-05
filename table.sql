CREATE TABLE chat_author (
	id serial PRIMARY KEY,
	name VARCHAR(30)
);

CREATE TABLE chat_message (
	id serial PRIMARY KEY,
	author_id INTEGER REFERENCES chat_author (id),
	message VARCHAR(140),
	sending_date TIMESTAMP
);

create sequence hibernate_sequence;
