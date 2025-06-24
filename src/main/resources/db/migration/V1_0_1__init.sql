create table users (
    id bigserial not null,
    email varchar(255) not null,
    password varchar(255) not null,
    full_name varchar(255),
    created_at timestamp(6) default CURRENT_TIMESTAMP,
    updated_at timestamp(6) default CURRENT_TIMESTAMP,
    constraint uq_users_email unique(email),
    constraint pk_users_id primary key (id)
);

create table user_roles (
    user_id bigint not null,
    roles varchar(255) check (roles in ('USER','ADMIN'))
);

create table refresh_tokens (
    id bigserial not null,
    token varchar(255) not null,
    expiry_date timestamp(6) with time zone not null,
    user_id bigint,
    created_at timestamp(6) default CURRENT_TIMESTAMP,
    updated_at timestamp(6) default CURRENT_TIMESTAMP,
    constraint uq_refresh_token_user_id unique(user_id),
    constraint uq_refresh_token_token unique(token),
    constraint pk_refresh_token_id primary key (id)
);

create table vinyl (
    id bigserial not null,
    album varchar(255) not null,
    label varchar(255) not null,
    country_of_origin varchar(255) not null,
    catalog_code varchar(255) not null,
    genre_id bigserial,
    condition varchar(255) not null,
    envelope_condition varchar(255) not null,
    price decimal(10, 2) not null,
    currency varchar(3) not null,
    "year" integer not null,
    artist varchar(255) not null,
    title varchar(255) not null,
    release_type varchar(255) not null,
    quantity integer not null,
    note text,
    created_at timestamp(6) default CURRENT_TIMESTAMP,
    updated_at timestamp(6) default CURRENT_TIMESTAMP,
    constraint pk_vinyl_id primary key (id)
);

alter table user_roles
add constraint fk_user_roles_users_id
foreign key (user_id) references users(id)
MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
