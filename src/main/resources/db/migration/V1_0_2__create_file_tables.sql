create table file_data (
    id bigserial not null,
    bytes bytea not null,
    constraint pk_file_data_id primary key (id)
);

create table file_metadatas (
    id bigint not null,
    size bigint not null,
    content_type varchar(255) not null,
    description text,
    name varchar(255) not null,
    original_name varchar(255) not null,
    hash varchar(40) not null,
    url varchar(255),
    content_url varchar(255),
    created_at timestamp(6) default CURRENT_TIMESTAMP,
    updated_at timestamp(6) default CURRENT_TIMESTAMP,
    constraint pk_file_metadatas_id primary key (id),
    constraint uq_file_metadatas_hash unique (hash)
);

create table files_references (
    id bigserial not null,
    file_id bigint not null,
    product_id bigint,
    constraint files_references_id primary key (id),
    constraint uq_files_references_product_id unique (file_id, product_id)
);

alter table file_metadatas
add constraint fk_file_metadatas_file_data
foreign key (id) references file_data(id)
on delete cascade;

alter table files_references
add constraint fk_files_references_file_data
foreign key (file_id) references file_data(id)
on delete cascade;

alter table files_references
add constraint fk_files_references_products
foreign key (product_id) references products(id)
on delete cascade;

DROP TRIGGER IF EXISTS trg_delete_orphaned_file_data ON files_references;
DROP FUNCTION IF EXISTS delete_orphaned_file_data;

CREATE FUNCTION delete_orphaned_file_data()
RETURNS trigger AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM files_references WHERE file_id = OLD.file_id
    ) THEN
        DELETE FROM file_data WHERE id = OLD.file_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_delete_orphaned_file_data
AFTER DELETE ON files_references
FOR EACH ROW
EXECUTE FUNCTION delete_orphaned_file_data();

