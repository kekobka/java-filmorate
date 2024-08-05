package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaRatingController {
    private final MpaRatingService service;

    @GetMapping
    public Collection<MpaRating> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MpaRating getById(@PathVariable @Positive long id) {
        return service.getById(id);
    }
}