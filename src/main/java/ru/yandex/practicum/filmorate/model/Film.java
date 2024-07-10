package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotation.DateAfter;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"name", "description", "releaseDate", "duration"})
public class Film {
    private Long id;

    @NotBlank()
    private String name;

    @Size(max = 200)
    private String description;

    @DateAfter(value = "1895.12.28") // date of the first movie
    private LocalDate releaseDate;

    @Positive()
    private Long duration;
}