create table file_data(
    id bigserial not null,
    bytes bytea not null,
    constraint pk_file_data_id primary key (id)
);

create table file_metadatas (
    id bigserial not null,
    file_data_id bigint not null,
    size bigint not null,
    content_type varchar(255) not null,
    description TEXT,
    name varchar(255) not null,
    original_name varchar(255) not null,
    url varchar(255),
    content_url varchar(255),
    created_at timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    constraint pk_file_metadatas_id primary key (id),
    constraint uq_file_metadatas_file_data_id unique(file_data_id)
);

create table vinil_images (
    file_metadata_id bigint not null,
    vinil_id bigint not null,
    constraint pk_vinil_images_file_metadata_id_vinil_id primary key (file_metadata_id, vinil_id),
    constraint uq_vinil_images_file_metadata_id unique(file_metadata_id)
);

alter table file_metadatas
add constraint fk_file_metadatas_file_data
foreign key (file_data_id) references file_data(id)
match simple on delete cascade;

alter table vinyl drop column image;