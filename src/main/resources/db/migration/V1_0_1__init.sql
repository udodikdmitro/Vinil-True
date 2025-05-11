create table users (
    id bigserial not null,
    email varchar(255) not null,
    password varchar(255) not null,
    full_name varchar(255),
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
    constraint uq_refresh_token_user_id unique(user_id),
    constraint uq_refresh_token_token unique(token),
    constraint pk_refresh_token_id primary key (id)
);

create table vinyl (
    id bigserial not null,
    year integer not null,
    artist varchar(255) not null,
    title varchar(255) not null,
    image bytea,
    constraint pk_vinyl_id primary key (id)
);

alter table user_roles
add constraint fk_user_roles_users_id
foreign key (user_id) references users(id)
MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
