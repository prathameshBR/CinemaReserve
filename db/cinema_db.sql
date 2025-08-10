CREATE DATABASE cinema_db;

USE cinema_db;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50)
);

CREATE TABLE movies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    total_seats INT,
    available_seats INT
);

CREATE TABLE bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    movie_id INT,
    seats_booked INT,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

INSERT INTO users (username, password) VALUES ('test', '1234');

INSERT INTO movies (title, total_seats, available_seats)
VALUES ('Avengers', 50, 50),
       ('Jawan', 40, 40),
       ('Pathaan', 60, 60);