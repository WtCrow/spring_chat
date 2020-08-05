#Simple WebSocket chat (spring+hibernate).

For start this project your need:

1) Enter in terminal: `cd /project/path`
2) Create data base and start code from table.sql
3) Define next environment variables:
- DB_USER
- DB_PASSWORD
- DB_URL (format for postgres: `jdbc:postgresql://HOST:PORT/DB_NAME`).
4) Change file /src/main/resources/hibernate.cfg.xml if you use not postgres
5) Enter in terminal `mvn spring-boot:run`
6) go to url localhost:8080
7) Profit!

P. S.
This chat very simple. Control by a password + login missing.
On start, you enter a nickname, even if nickname already used you be get access.
