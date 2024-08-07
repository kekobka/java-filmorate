package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.annotation.DateAfter;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.MpaRating;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmTest {
    private static final Validator validator;

    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    static Film getFilm() {
        return Film.builder()
                .id(1)
                .name("test")
                .duration(1L)
                .releaseDate(LocalDate.now())
                .description("desc")
                .mpa(new MpaRating(1, "G"))
                .build();
    }

    @Test
    void shouldNotValidateBlankName() {
        Film film = getFilm();
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void shouldNotValidateDescriptionMore200Symbols() {
        Film film = getFilm();
        film.setDescription("1".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(Size.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("description", violation.getPropertyPath().toString());
    }

    @Test
    void shouldValidateDescription() {
        Film film = getFilm();
        film.setDescription("1".repeat(200));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotValidateReleaseDateBefore28_12_1895() {
        Film film = getFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(DateAfter.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("releaseDate", violation.getPropertyPath().toString());
    }

    @Test
    void shouldValidateReleaseDate() {
        Film film = getFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 29));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        System.out.println(violations.toString());
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotValidateNegativeDuration() {
        Film film = getFilm();
        film.setDuration(-1L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(Positive.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("duration", violation.getPropertyPath().toString());
    }

    @Test
    void shouldNotValidateZeroDuration() {
        Film film = getFilm();
        film.setDuration(0L);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals(Positive.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("duration", violation.getPropertyPath().toString());
    }

    @Test
    void shouldValidate() {
        Film film = getFilm();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }
}