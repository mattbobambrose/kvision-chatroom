DROP SCHEMA IF EXISTS chatroom CASCADE;
CREATE SCHEMA chatroom;
CREATE TABLE chatroom.messages
(
    id         SERIAL PRIMARY KEY,
    username   TEXT      NOT NULL,
    room       TEXT      NOT NULL,
    message    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);