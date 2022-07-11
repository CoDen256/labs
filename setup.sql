
CREATE DATABASE IF NOT EXISTS db;
use db;
CREATE TABLE IF NOT EXISTS passwords (
    password varchar(255)
);

INSERT INTO passwords VALUES("12345");
SELECT * FROM passwords;