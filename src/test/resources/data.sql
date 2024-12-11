INSERT INTO books(book_id, book_author, book_title, book_detail)
VALUES ('B101', 'James Clear', 'Atomic Habits', '{"publishing year": "2012", "genre": "Self-Help"}'::jsonb);

INSERT INTO books(book_id, book_author, book_title, book_detail)
VALUES ('B102', 'Paulo Coelho', 'The Alchemist', '{"publishing year": "1988", "genre": "Novel"}'::jsonb);

INSERT INTO books(book_id, book_author, book_title, book_detail)
VALUES ('B103', 'Brain Tracy', 'Eat That Frog', '{"publishing year": "2001", "genre": "Self-Help"}'::jsonb);

INSERT INTO books(book_id, book_author, book_title, book_detail)
VALUES ('B104', 'Cal Newport', 'Deep Work', '{"publishing year": "2016", "genre": "Self-Help"}'::jsonb);

INSERT INTO books(book_id, book_author, book_title, book_detail)
VALUES ('B105', 'Darius Foroux', 'Do It Today', '{"publishing year": "2018", "genre": "Motivational"}');

INSERT INTO authors(author_id, author_name)
VALUES ('A101', 'James Clear');

INSERT INTO authors(author_id, author_name)
VALUES ('A102', 'Paulo Coelho');

INSERT INTO authors(author_id, author_name)
VALUES ('A103', 'Darius Foroux');

INSERT INTO authors(author_id, author_name)
VALUES ('A104', 'Brain Tracy');

INSERT INTO authors(author_id, author_name)
VALUES ('A105', 'Cal Newport');