--liquibase formatted sql

--changeset fmi:002-create-hall-table
CREATE TABLE hall (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL
);

--rollback DROP TABLE hall;