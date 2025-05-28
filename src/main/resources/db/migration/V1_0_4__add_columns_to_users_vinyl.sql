alter table users add column currency varchar(3) not null;

alter table vinyl add column price decimal(10, 2) not null;
alter table vinyl add column currency varchar(3) not null;
alter table vinyl add column original_price decimal(10, 2) not null;
alter table vinyl add column original_currency varchar(3) not null;
alter table vinyl add column quantity integer not null default 0;