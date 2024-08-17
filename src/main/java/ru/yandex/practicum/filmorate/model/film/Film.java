package ru.yandex.practicum.filmorate.model.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotation.DateAfter;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(of = {"name", "description", "releaseDate", "duration"})
public class Film {
    private Integer id;

    @NotBlank()
    private String name;

    @Size(max = 200)
    private String description;

    @DateAfter(value = "1895.12.28") // date of the first movie
    private LocalDate releaseDate;

    @Positive()
    private Long duration;

    @NotNull
    private MpaRating mpa;

    private Set<Integer> likes = new HashSet<>();

    private Set<Genre> genres = new TreeSet<>();

    @Builder
    public Film(int id, Long duration, LocalDate releaseDate, String description, String name, MpaRating mpa) {
        this.id = id;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.description = description;
        this.name = name;
        this.mpa = mpa;
    }

    public Map<String, Object> toMap() {

        Map<String, Object> values = new HashMap<>();
        values.put("duration", duration);
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("rating_id", mpa.getId());
        return values;
    }
}