package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();
    private long seq = 0;

    private long generateId() {
        return ++seq;
    }

    @Override
    public Optional<Film> get(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public boolean contains(long id) {
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
    public void addLike(long filmId, long userId) {
        Set<Long> usersLikes = filmLikes.computeIfAbsent(filmId, k -> new HashSet<>());
        usersLikes.add(userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
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
