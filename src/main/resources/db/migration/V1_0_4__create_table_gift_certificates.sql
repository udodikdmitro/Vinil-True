create table gift_certificates (
    id bigint not null,
    code varchar(255) not null,
    "value" decimal(10, 2) not null,
    is_used boolean not null,
    issued_to_id bigserial not null,
    expires_at timestamp(6) not null,
    constraint pk_gift_certificates_id primary key (id)
);

alter table gift_certificates
add constraint uq_gift_certificates_code
unique (code);

alter table gift_certificates
add constraint fk_gift_certificates_products_id
FOREIGN KEY (id) REFERENCES products(id)
MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE;

alter table gift_certificates
add constraint fk_gift_certificates_users_issued_to_id
FOREIGN KEY (issued_to_id) REFERENCES users(id)
MATCH SIMPLE ON UPDATE NO ACTION ON DELETE CASCADE;