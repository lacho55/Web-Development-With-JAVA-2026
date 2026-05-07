--liquibase formatted sql

--changeset fmi:004-add-version-to-performance
ALTER TABLE performance ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

--rollback ALTER TABLE performance DROP COLUMN version;