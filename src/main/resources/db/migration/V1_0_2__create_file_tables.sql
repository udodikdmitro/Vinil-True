create table file_data (
    id bigserial not null,
    bytes bytea not null,
    constraint pk_file_data_id primary key (id)
);

create table file_metadatas (
    id bigserial not null,
    file_data_id bigint not null,
    size bigint not null,
    content_type varchar(255) not null,
    description text,
    name varchar(255) not null,
    original_name varchar(255) not null,
    url varchar(255),
    content_url varchar(255),
    created_at timestamp(6) default CURRENT_TIMESTAMP,
    updated_at timestamp(6) default CURRENT_TIMESTAMP,
    constraint pk_file_metadatas_id primary key (id),
    constraint uq_file_metadatas_file_data_id unique(file_data_id)
);

create table files_references (
    file_metadata_id bigint not null primary key,
    vinyl_id bigint
);

alter table vinyl drop column if exists image;

alter table file_metadatas
add constraint fk_file_metadatas_file_data
foreign key (file_data_id) references file_data(id)
on delete cascade;

alter table files_references
add constraint fk_files_references_file_metadatas
foreign key (file_metadata_id) references file_metadatas(id)
on delete cascade;

alter table files_references
add constraint fk_files_references_vinyl
foreign key (vinyl_id) references vinyl(id)
on delete cascade;

create or replace function delete_orphan_file_metadata() returns trigger as $$
begin
  delete from file_metadatas where id = OLD.file_metadata_id;
  return null;
end;
$$ language plpgsql;

create trigger trg_delete_orphan_file_metadata
after delete on files_references
for each row
execute function delete_orphan_file_metadata();