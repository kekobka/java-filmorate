CREATE TABLE IF NOT EXISTS friends (
    user_id integer,
    other_user_id integer
);

CREATE TABLE IF NOT EXISTS mpa_rating (
    rating_id integer PRIMARY KEY,
    name varchar
);

CREATE TABLE IF NOT EXISTS users (
    user_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    login varchar,
    name varchar,
    email varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id integer,
    genre_id integer
);

CREATE TABLE IF NOT EXISTS films (
    film_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL,
    description varchar,
    duration integer,
    release_date date,
    rating_id integer DEFAULT 6
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id integer PRIMARY KEY,
    name varchar
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id integer,
    user_id integer
);