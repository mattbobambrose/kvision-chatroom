DROP SCHEMA IF EXISTS chatroom CASCADE;
CREATE SCHEMA chatroom;

CREATE TABLE chatroom.rooms
(
    id         SERIAL PRIMARY KEY,
    name       TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO chatroom.rooms (name)
VALUES ('Sales');
INSERT INTO chatroom.rooms (name)
VALUES ('Marketing');
INSERT INTO chatroom.rooms (name)
VALUES ('Engineering');

CREATE TABLE chatroom.messages
(
    id         SERIAL PRIMARY KEY,
    username   TEXT      NOT NULL,
    room_ref   INT REFERENCES chatroom.rooms ON DELETE CASCADE,
    message    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE VIEW chatroom.messages_view AS
SELECT m.id,
       m.username,
       m.message,
       m.created_at,
       r.name AS room_name
FROM chatroom.messages m
         JOIN chatroom.rooms r ON r.id = m.room_ref
ORDER BY m.created_at DESC;
