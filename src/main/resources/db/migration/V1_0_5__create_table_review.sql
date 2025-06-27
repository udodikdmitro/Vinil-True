create table reviews (
    id bigserial not null,
    product_id bigserial,
    user_id bigserial,
    rating integer not null,
    comment TEXT,
    created_at timestamp(6) default CURRENT_TIMESTAMP,
    updated_at timestamp(6) default CURRENT_TIMESTAMP,
    constraint pk_reviews_id primary key (id),
    constraint uq_reviews_user_id_product_id unique (product_id, user_id)
);

alter table reviews
add constraint fk_reviews_products_id
foreign key (product_id) references products(id)
MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE;

alter table reviews
add constraint fk_reviews_users_id
foreign key (user_id) references users(id)
MATCH SIMPLE ON UPDATE NO ACTION ON DELETE SET NULL;

create index idx_reviews_product_id on reviews(product_id);