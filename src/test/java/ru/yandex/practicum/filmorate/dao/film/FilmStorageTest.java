package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmStorageTest {
    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;

    @Test
    @Order(1)
    void postAndGetTest() {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(120L)
                .releaseDate(LocalDate.of(2000, 10, 10))
                .mpa(new MpaRating(1, "G"))
                .build();

        film.getGenres().add(new Genre(1));

        Film expectedFilm = Film.builder()
                .id(1)
                .name("name")
                .description("desc")
                .duration(120L)
                .releaseDate(LocalDate.of(2000, 10, 10))
                .mpa(new MpaRating(1, "G"))
                .build();

        expectedFilm.getGenres().add(new Genre(1, "Комедия"));

        Film returnedfilm = filmStorage.save(film);
        Assertions.assertEquals(expectedFilm, returnedfilm);
    }

    @Test
    @Order(2)
    void updateTest() {
        Film film = Film.builder()
                .id(1)
                .name("name")
                .description("desc")
                .duration(120L)
                .releaseDate(LocalDate.of(2000, 10, 10))
                .mpa(new MpaRating(2, "PG"))
                .build();

        Film expectedFilm = Film.builder()
                .id(1)
                .name("name")
                .description("desc")
                .duration(120L)
                .releaseDate(LocalDate.of(2000, 10, 10))
                .mpa(new MpaRating(2, "PG"))
                .build();

        expectedFilm.getGenres().add(new Genre(1, "Комедия"));
        Assertions.assertEquals(expectedFilm, filmStorage.update(film));
    }

    @Test
    @Order(3)
    void getAllTest() {
        Assertions.assertEquals(1, filmStorage.getAll().size());
    }

    @Test
    @Order(4)
    void addLikeTest() {
        User user = User.builder()
                .name("name")
                .login("login")
                .email("login@yandex.ru")
                .birthday(LocalDate.of(1999, 12, 21))
                .build();

        userStorage.save(user);

        filmStorage.addLike(1, 1);
        Assertions.assertEquals(1, filmStorage.getById(1).get().getLikes().size());
    }

    @Test
    @Order(5)
    void removeLikeTest() {
        filmStorage.deleteLike(1, 1);
        Assertions.assertEquals(0, filmStorage.getById(1).get().getLikes().size());
    }
}