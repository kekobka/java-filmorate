package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.mpaRating.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.util.Collection;

@Service
public class MpaRatingService {
    private final MpaRatingDbStorage storage;

    @Autowired
    public MpaRatingService(MpaRatingDbStorage storage) {
        this.storage = storage;
    }

    public Collection<MpaRating> getAll() {
        return storage.getAll();
    }

    public MpaRating getById(long id) {
        return storage.getById(id);
    }

}