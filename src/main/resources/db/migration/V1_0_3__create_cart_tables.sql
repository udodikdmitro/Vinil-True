create table carts (
    id bigserial not null,
    user_id bigint not null,
    created_at timestamp(6),
    updated_at timestamp(6),
    constraint pk_carts_id primary key (id),
    constraint uq_carts_user_id unique(user_id)
);

create table cart_items(
    id bigserial not null,
    quantity integer not null,
    vinyl_id bigint,
    cart_id bigint,
    created_at timestamp(6),
    updated_at timestamp(6),
    constraint pk_cart_items_id primary key (id),
    constraint uq_cart_items_cart_id_vinyl_id unique (cart_id, vinyl_id)
);

alter table carts
add constraint fk_carts_users_id
foreign key (user_id) references users(id)
on delete cascade;

alter table cart_items
add constraint fk_cart_items_carts_id
foreign key (cart_id) references carts(id)
on delete cascade;

alter table cart_items
add constraint fk_cart_items_vinyl_id
foreign key (vinyl_id) references vinyl(id)
on delete cascade;
