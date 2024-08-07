package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .duration(rs.getLong("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .description(rs.getString("description"))
                .build();
    }
}