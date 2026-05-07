--liquibase formatted sql

--changeset fmi:001-create-show-table
CREATE TABLE show (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    genre VARCHAR(20) NOT NULL,
    duration_minutes INT NOT NULL,
    age_rating VARCHAR(10) NOT NULL
);

--rollback DROP TABLE show;