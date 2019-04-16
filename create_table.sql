DROP DATABASE IF EXISTS moviedb;

CREATE DATABASE moviedb;
USE moviedb;

CREATE TABLE movies (
	id varchar(10) PRIMARY KEY,
	title varchar(100) NOT NULL,
    year integer NOT NULL,
    director varchar(100) NOT NULL
    );
    
CREATE TABLE stars (
	id varchar(10) PRIMARY KEY,
    name varchar(100) NOT NULL,
    birthYear integer
    );
    
CREATE TABLE stars_in_movies (
	starId varchar(10) REFERENCES stars.id,
    movieId varchar(10) REFERENCES movies.id
    );
    
CREATE TABLE genres (
	id integer PRIMARY KEY AUTO_INCREMENT,
    name varchar(32) NOT NULL
    );
    
CREATE TABLE genres_in_movies (
	genreId integer REFERENCES genres.id,
    movieId varchar(10) REFERENCES movies.id
    );
    
CREATE TABLE customers (
	id integer PRIMARY KEY AUTO_INCREMENT,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    ccId varchar(20) REFERENCES creditcards.id,
    address varchar(200) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(20) NOT NULL
    );
    
CREATE TABLE sales (
	id integer PRIMARY KEY AUTO_INCREMENT,
    customerId integer REFERENCES customers.id,
    movieId varchar(10) REFERENCES movies.id,
    saleData date NOT NULL
    );
    
CREATE TABLE creditcards (
	id varchar(20) PRIMARY KEY,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    expiration date NOT NULL
    );
    
CREATE TABLE ratings (
	movieId varchar(10) REFERENCES movies.id,
    rating float NOT NULL,
    numVotes integer NOT NULL
    );
    
CREATE TABLE employees (
	email varchar(50) PRIMARY KEY,
	password varchar(20) NOT NULL,
	fullname varchar(100)
	);
