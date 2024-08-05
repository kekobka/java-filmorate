package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film save(Film film) {
        log.info("POST /films => {}", film);
        return filmStorage.save(film);
    }

    public List<Film> getAll() {
        log.info("GET /films");
        return filmStorage.getAll();
    }

    public Film update(Film film) {
        log.info("PUT /films => {}", film);
        if (!filmStorage.contains(film.getId())) {
            throw new NotFoundException(String.format("film: %d not found", film.getId()));
        }
        return filmStorage.update(film);
    }

    public void addLike(long id, long userId) {
        log.info("PUT /films/{}/like/{}", id, userId);

        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("film: %d not found", userId));
        }
        if (!filmStorage.contains(id)) {
            throw new NotFoundException(String.format("film: %d not found", id));
        }
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(long filmId, long userId) {
        log.info("DELETE /films/{}/like/{}", filmId, userId);

        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("film: %d not found", userId));
        }
        if (!filmStorage.contains(filmId)) {
            throw new NotFoundException(String.format("film: %d not found", filmId));
        }

        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        log.info("GET /films/popular?count={}", count);
        return filmStorage.getPopular(count);
    }
}