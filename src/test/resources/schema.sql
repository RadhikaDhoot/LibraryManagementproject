create table books (
    book_id varchar(50) primary key,
    book_author varchar(50) not null,
    book_title varchar(50) not null,
    book_detail JSONB not null
);

create table authors (
    author_id varchar(10) primary key,
    author_name varchar(50) not null
);
