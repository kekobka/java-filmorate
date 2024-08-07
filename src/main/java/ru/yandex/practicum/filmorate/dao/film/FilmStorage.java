package ru.yandex.practicum.filmorate.dao.film;

import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;

public interface FilmStorage {

    Film getById(int id);

    boolean contains(int id);

    List<Film> getAll();

    Film save(Film film);

    Film update(Film film);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getPopular(Integer count);

}