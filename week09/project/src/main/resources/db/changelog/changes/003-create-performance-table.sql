--liquibase formatted sql

--changeset fmi:003-create-performance-table
CREATE TABLE performance (
    id BIGSERIAL PRIMARY KEY,
    show_id BIGINT NOT NULL,
    hall_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_performance_show FOREIGN KEY (show_id) REFERENCES show(id),
    CONSTRAINT fk_performance_hall FOREIGN KEY (hall_id) REFERENCES hall(id)
);

--rollback DROP TABLE performance;