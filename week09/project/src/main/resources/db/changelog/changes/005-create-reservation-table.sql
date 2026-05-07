--liquibase formatted sql

--changeset fmi:005-create-reservation-table
CREATE TABLE reservation (
    id BIGSERIAL PRIMARY KEY,
    performance_id BIGINT NOT NULL,
    seat_label VARCHAR(20) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    reserved_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_reservation_performance FOREIGN KEY (performance_id) REFERENCES performance(id),
    CONSTRAINT uq_reservation_seat UNIQUE (performance_id, seat_label)
);

--rollback DROP TABLE reservation;