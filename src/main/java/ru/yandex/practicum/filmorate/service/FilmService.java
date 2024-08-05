package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.FilmStorage;
import ru.yandex.practicum.filmorate.dao.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film save(Film film) {
        log.info("POST /films => {}", film);
        return filmStorage.save(film);
    }

    public List<Film> getAll() {
        log.info("GET /films");
        return filmStorage.getAll();
    }

    public Optional<Film> getById(int id) {
        log.info("GET /films/{}", id);
        return filmStorage.getById(id);
    }

    public Film update(Film film) {
        log.info("PUT /films => {}", film);
        return filmStorage.update(film);
    }

    public void addLike(int id, int userId) {
        log.info("PUT /films/{}/like/{}", id, userId);
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(int filmId, int userId) {
        log.info("DELETE /films/{}/like/{}", filmId, userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        log.info("GET /films/popular?count={}", count);
        return filmStorage.getPopular(count);
    }
}