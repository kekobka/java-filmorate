package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long seq = 0;

    private long generateId() {
        return ++seq;
    }

    private void validateUniqueness(Film film) {
        for (Film existedFilm : films.values()) {
            if (film.getName().equals(existedFilm.getName())) {
                throw new ValidationException("Такой фильм уже существует.");
            }
        }
    }

    private void addFilm(Film film) {
        validateUniqueness(film);
        film.setId(generateId());
        films.put(film.getId(), film);
    }

    private void updateFilm(Film film, Film oldFilm) {
        if (film.getName() != null) {
            oldFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            oldFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            oldFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() != null) {
            oldFilm.setDuration(film.getDuration());
        }
    }

    @GetMapping
    public Collection<Film> get() {
        log.info("GET /films");
        return films.values();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        try {
            log.info("POST /films => {}", film);
            addFilm(film);
            return film;
        } catch (Exception e) {
            log.error("Ошибка при создании фильма.", e);
            throw e;
        }
    }

    @PutMapping
    public Film update(@Validated @RequestBody Film film) {
        try {
            log.info("PUT /films => ID = {}, {}", film.getId(), film);
            Film oldFilm = films.get(film.getId());
            updateFilm(film, oldFilm);
            return oldFilm;
        } catch (Exception e) {
            log.error("Ошибка при обновлении фильма.", e);
            throw e;
        }
    }


}