CREATE TABLE users
(id INTEGER PRIMARY KEY,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 email VARCHAR(30) UNIQUE,
 phone VARCHAR(30),
 role VARCHAR(30),
 pass VARCHAR(300));
