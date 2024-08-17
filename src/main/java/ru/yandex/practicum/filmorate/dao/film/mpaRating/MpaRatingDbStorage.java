package ru.yandex.practicum.filmorate.dao.film.mpaRating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.Collection;

@Repository
@Slf4j
public class MpaRatingDbStorage {
    private final JdbcTemplate jdbc;
    private final MpaRatingRowMapper mpaRatingRowMapper;

    public MpaRatingDbStorage(JdbcTemplate jdbc, MpaRatingRowMapper ratingRowMapper) {
        this.jdbc = jdbc;
        this.mpaRatingRowMapper = ratingRowMapper;
    }

    public Collection<MpaRating> getAll() {
        return jdbc.query("SELECT * FROM mpa_rating", mpaRatingRowMapper);
    }

    public MpaRating getById(long id) {
        try {
            return jdbc.queryForObject("SELECT * FROM mpa_rating WHERE rating_id = ?", mpaRatingRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("mpa rating %d not found", id));
        }
    }

    public boolean contains(int id) {
        try {
            return getById(id) != null;
        } catch (NotFoundException e) {
            return false;
        }
    }
}