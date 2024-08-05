package ru.yandex.practicum.filmorate.dao.film.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Collection;

@Repository
@Slf4j
public class GenreDbStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper genreRowMapper;

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper genreRowMapper) {
        this.jdbc = jdbc;
        this.genreRowMapper = genreRowMapper;
    }

    public Collection<Genre> getAll() {
        return jdbc.query("SELECT * FROM genre", genreRowMapper);
    }

    public Genre getById(long id) {
        try {
            return jdbc.queryForObject("SELECT * FROM genre WHERE genre.genre_id = ?", genreRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("genre %d not found", id));
        }
    }

}