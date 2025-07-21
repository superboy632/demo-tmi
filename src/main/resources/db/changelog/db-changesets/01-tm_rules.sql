--liquibase formatted sql
--changeSet gnikitin:rule-02
CREATE TABLE rule
(
    id uuid primary key not null,
    agent_rule_id     integer             not null,
    agent_type        varchar(255)         not null,
    agent_id          integer              not null,
    device_name       varchar(255)         not null,
    device_id         integer,
    device_address    varchar(255),
    active            boolean           not null
);