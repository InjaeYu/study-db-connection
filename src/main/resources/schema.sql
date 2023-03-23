drop table if exists member cascade;

create table member (
--     id int unsigned auto_increment,
    id int auto_increment,
    name varchar(255) not null,
--     age int unsigned not null default 0,
    age int not null default 0,
    city varchar(255),
    street varchar(255),
    zip_code varchar(18),
    created_date timestamp,
    last_modified_date timestamp,
    primary key (id)
);

drop table if exists pet cascade;

create table pet (
--     id int unsigned auto_increment,
    id int auto_increment,
    name varchar(255) not null,
    species varchar(255) not null,
--     age int unsigned not null default 0,
    age int not null default 0,
    member_id int not null,
    created_date timestamp,
    last_modified_date timestamp,
    primary key (id),
    foreign key (member_id) references member(id)
)
