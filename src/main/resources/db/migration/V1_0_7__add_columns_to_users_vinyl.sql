alter table users add column currency varchar(3) not null;

alter table products add column original_price decimal(10, 2) not null;
alter table products add column original_currency varchar(3) not null;