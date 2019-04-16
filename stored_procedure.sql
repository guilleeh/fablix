DELIMITER $$

DROP PROCEDURE IF EXISTS add_movie $$
CREATE PROCEDURE add_movie(IN movie_id VARCHAR(9), IN title VARCHAR(100), IN year INTEGER, IN director VARCHAR(100), IN genre_id INTEGER, IN star_id VARCHAR(10))
BEGIN
INSERT INTO movies VALUES(movie_id, title, year, director);
INSERT INTO genres_in_movies VALUES(genre_id, movie_id);
INSERT INTO stars_in_movies VALUES(star_id, movie_id);
END $$

DELIMITER ;