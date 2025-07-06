create table genres (
    id bigserial not null,
    name_en varchar(255) not null,
    name_uk varchar(255) not null,
    created_at timestamp(6) default CURRENT_TIMESTAMP,
    updated_at timestamp(6) default CURRENT_TIMESTAMP,
    constraint pk_genres_id primary key (id),
    constraint uq_genres_name_en_name_uk unique (name_en, name_uk)
);

alter table vinyl
add constraint fk_vinyl_genres_id
foreign key (genre_id) references genres(id)
MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;