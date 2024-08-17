package ru.yandex.practicum.filmorate.dao.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.film.genre.GenreRowMapper;
import ru.yandex.practicum.filmorate.dao.film.mpaRating.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.dao.user.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.WrongArgumentException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Like;
import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;
    private final UserRowMapper userRowMapper;
    private final FilmRowMapper filmRowMapper;
    private final MpaRatingRowMapper mpaRatingRowMapper;
    private final GenreRowMapper genreRowMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, UserRowMapper userRowMapper, FilmRowMapper filmRowMapper, MpaRatingRowMapper ratingRowMapper, GenreRowMapper genreRowMapper) {
        this.jdbc = jdbc;
        this.userRowMapper = userRowMapper;
        this.filmRowMapper = filmRowMapper;
        this.mpaRatingRowMapper = ratingRowMapper;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public Film getById(int filmId) {
        try {
            Film film = jdbc.queryForObject("SELECT * FROM films WHERE film_id = ?", filmRowMapper, filmId);
            assert film != null;
            film.setMpa(jdbc.queryForObject("SELECT * FROM mpa_rating WHERE rating_id = ?",
                    mpaRatingRowMapper, jdbc.queryForObject("SELECT rating_id FROM films WHERE film_id = ?", Long.class, filmId)));
            film.setLikes(new HashSet<>(jdbc.queryForList("SELECT user_id FROM film_likes WHERE film_id = ?", Integer.class, filmId)));
            film.setGenres(new TreeSet<>(jdbc.query("SELECT g.* FROM genre AS g INNER JOIN film_genre fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?",
                    genreRowMapper, filmId)));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("film %d not found", filmId));
        }
    }

    @Override
    public boolean contains(int id) {
        try {
            return getById(id) != null;
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = jdbc.query("SELECT * FROM films", filmRowMapper);
        Map<Long, MpaRating> ratings = jdbc.query("SELECT * FROM mpa_rating", mpaRatingRowMapper)
                .stream()
                .collect(Collectors.toMap(MpaRating::getId, Function.identity()));

        Map<Integer, Long> filmRatings = jdbc.query("SELECT film_id, rating_id FROM films",
                        (rs, rowNum) -> Map.entry(rs.getInt("film_id"), rs.getLong("rating_id")))
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<Integer, Set<Integer>> likesMap = jdbc.query("SELECT film_id, user_id FROM film_likes",
                        (rs, rowNum) -> Map.entry(rs.getInt("film_id"), rs.getInt("user_id")))
                .stream()
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));
        Map<Integer, Set<Genre>> genresMap = jdbc.query("SELECT g.*, fg.film_id FROM genre g INNER JOIN film_genre fg ON g.genre_id = fg.genre_id",
                        (rs, rowNum) -> Map.entry(rs.getInt("film_id"), Objects.requireNonNull(genreRowMapper.mapRow(rs, rowNum))))
                .stream()
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));

        films.forEach(film -> {
            film.setMpa(ratings.get(filmRatings.get(film.getId())));
            film.setLikes(likesMap.getOrDefault(film.getId(), new HashSet<>()));
            film.setGenres(genresMap.getOrDefault(film.getId(), new HashSet<>()));
        });

        return films;
    }

    @Override
    public Film save(Film film) {
        try {
            jdbc.queryForObject("SELECT * FROM mpa_rating WHERE rating_id = ?", mpaRatingRowMapper, film.getMpa().getId());
        } catch (EmptyResultDataAccessException e) {
            throw new WrongArgumentException(String.format("mpa rating %d not found", film.getMpa().getId()));
        }

        Set<Genre> genres = film.getGenres();
        for (Genre genre : genres) {
            try {
                jdbc.queryForObject("SELECT * FROM genre WHERE genre_id = ?", genreRowMapper, genre.getId());
            } catch (EmptyResultDataAccessException e) {
                throw new WrongArgumentException(String.format("genre %d not found", genre.getId()));
            }
        }

        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc).withTableName("films").usingGeneratedKeyColumns("film_id");
            int id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();

            SimpleJdbcInsert insertGenres = new SimpleJdbcInsert(jdbc).withTableName("film_genre");

            for (Genre genre : genres) {

                Map<String, Object> genreMap = new HashMap<>();
                genreMap.put("film_id", id);
                genreMap.put("genre_id", genre.getId());
                insertGenres.execute(genreMap);
            }
            film.setId(id);
            return film;

        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public Film update(Film film) {
        try {

            int updatedRow = jdbc.update("UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, rating_id = ? WHERE film_id = ?",
                    film.getName(),
                    film.getDescription(),
                    film.getDuration(),
                    film.getReleaseDate(),
                    film.getMpa().getId(),
                    film.getId());
            if (updatedRow == 0) {
                throw new NotFoundException(String.format("film %d not found", film.getId()));
            }

            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("film %d not found", film.getId()));
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        try {

            if (!contains(filmId)) {
                throw new NotFoundException(String.format("film %d not found", filmId));
            }

            try {
                jdbc.queryForObject("SELECT * FROM users WHERE user_id = ?;", userRowMapper, userId);
            } catch (EmptyResultDataAccessException e) {
                throw new NotFoundException(String.format("user: %d not found", userId));
            }

            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc).withTableName("film_likes");
            int updated = simpleJdbcInsert.execute(Like.builder().filmId(filmId).userId(userId).build().toMap());

            if (updated == 0) {
                throw new InternalErrorException("update failed");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException("update failed");
        }
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        try {

            if (!contains(filmId)) {
                throw new NotFoundException(String.format("film %d not found", filmId));
            }

            try {
                jdbc.queryForObject("SELECT * FROM users WHERE user_id = ?;", userRowMapper, userId);
            } catch (EmptyResultDataAccessException e) {
                throw new NotFoundException(String.format("user: %d not found", userId));
            }

            jdbc.update("DELETE FROM film_likes WHERE film_id = ? AND user_id = ?", filmId, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException("update failed");
        }
    }

    @Override
    public List<Film> getPopular(Integer count) {
        List<Film> films = getAll();

        Comparator<Film> comparator = Comparator.comparing(film -> film.getLikes().size());
        return films.stream().sorted(comparator.reversed()).limit(count).toList();
    }
}

