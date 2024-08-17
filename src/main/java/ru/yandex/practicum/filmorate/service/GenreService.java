package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreDbStorage storage;

    @Autowired
    public GenreService(GenreDbStorage storage) {
        this.storage = storage;
    }

    public Collection<Genre> getAll() {
        return storage.getAll();
    }

    public Genre getById(long id) {
        return storage.getById(id);
    }
}