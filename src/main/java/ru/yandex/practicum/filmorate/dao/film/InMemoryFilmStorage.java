package ru.yandex.practicum.filmorate.dao.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.*;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> filmLikes = new HashMap<>();
    private int seq = 0;

    private int generateId() {
        return ++seq;
    }

    @Override
    public Optional<Film> getById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public boolean contains(int id) {
        return films.containsKey(id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film save(Film film) {
        if (films.get(film.getId()) == null) {
            film.setId(generateId());
        }
        films.put(film.getId(), film);
        return film;
    }

    private void updateFilm(Film oldFilm, Film film) {
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

    @Override
    public Film update(Film film) {
        if (!contains(film.getId())) {
            throw new NotFoundException(String.format("film: %d not found", film.getId()));
        }
        updateFilm(films.get(film.getId()), film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {

        Set<Integer> usersLikes = filmLikes.computeIfAbsent(filmId, k -> new HashSet<>());
        usersLikes.add(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        if (!filmLikes.containsKey(filmId)) {
            throw new NotFoundException(String.format("film %d not found", filmId));
        }

        if (!filmLikes.get(filmId).contains(userId)) {
            throw new NotFoundException("not liked by " + userId);
        }

        filmLikes.get(filmId).remove(userId);
    }

    @Override
    public List<Film> getPopular(Integer count) {
        List<Film> topFilms = filmLikes.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
                .map(Map.Entry::getKey)
                .map(films::get)
                .toList()
                .reversed();
        return topFilms.subList(0, Math.min(count, filmLikes.size()));
    }
}
