CREATE TABLE addresses
(
    id            BIGINT PRIMARY KEY,
    city          VARCHAR(255),
    street        VARCHAR(255),
    house_number  VARCHAR(255),
    apartment     VARCHAR(255),
    method        VARCHAR(255),
    provider_id   BIGINT,
    branch_number VARCHAR(255),
    is_default    boolean,
    create_at     TIMESTAMP,
    update_at     TIMESTAMP
);

CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255),
    phone_number  VARCHAR(50),
    full_name     VARCHAR(255),
    role          VARCHAR(50),
    is_verified   BOOLEAN,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    password_hash VARCHAR(255),
    is_blocked    BOOLEAN,
    address_id    BIGINT,
    CONSTRAINT fk_user_address FOREIGN KEY (address_id) REFERENCES addresses (id)
);


create table user_roles
(
    user_id bigint not null,
    roles   varchar(255) check (roles in ('USER', 'ADMIN'))
);

create table refresh_tokens
(
    id          bigserial                   not null,
    token       varchar(255)                not null,
    created_at  timestamp,
    expiry_date timestamp(6) with time zone not null,
    user_id     bigint,
    constraint uq_refresh_token_user_id unique (user_id),
    constraint uq_refresh_token_token unique (token),
    constraint pk_refresh_token_id primary key (id)
);

create table products
(
    id         bigserial      not null,
    title      varchar(255)   not null,
    price      decimal(10, 2) not null,
    currency   varchar(3)     not null,
    quantity   integer        not null,
    created_at timestamp(6) default CURRENT_TIMESTAMP,
    updated_at timestamp(6) default CURRENT_TIMESTAMP,
    constraint pk_products_id primary key (id)
);

create table vinyl
(
    id                 bigserial    not null,
    album              varchar(255) not null,
    label              varchar(255) not null,
    country_of_origin  varchar(255) not null,
    catalog_code       varchar(255) not null,
    genre_id           bigserial,
    condition          varchar(255) not null,
    envelope_condition varchar(255) not null,
    "year"             integer      not null,
    artist             varchar(255) not null,
    release_type       varchar(255) not null,
    note               text,
    constraint pk_vinyl_id primary key (id)
);

alter table user_roles
    add constraint fk_user_roles_users_id
        foreign key (user_id) references users (id)
            MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

alter table vinyl
    add constraint fk_vinyl_products_id
        FOREIGN KEY (id) REFERENCES products (id)
            MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;