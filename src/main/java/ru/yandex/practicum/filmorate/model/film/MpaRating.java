package ru.yandex.practicum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MpaRating {
    private long id;
    private String name;

    @Builder
    public MpaRating(long id, String name) {
        this.id = id;
        this.name = name;
    }
}