create table carts (
    id bigserial not null,
    user_id bigint not null,
    currency varchar(255) not null,
    created_at timestamp(6),
    updated_at timestamp(6),
    constraint pk_carts_id primary key (id),
    constraint uq_carts_user_id unique(user_id)
);

create table cart_items(
    id bigserial not null,
    quantity integer not null,
    product_id bigint,
    cart_id bigint,
    constraint pk_cart_items_id primary key (id),
    constraint uq_cart_items_cart_id_product_id unique (cart_id, product_id)
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
add constraint fk_cart_items_product_id
foreign key (product_id) references products(id)
on delete cascade;
