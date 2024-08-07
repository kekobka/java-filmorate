package ru.yandex.practicum.filmorate.dao.user;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserStorageTest {
    @Autowired
    private final UserDbStorage userStorage;

    @Test
    @Order(1)
    void saveTest() {
        User user = new User(-1, LocalDate.of(1999, 12, 21), "name", "login", "login@yandex.ru");

        User expectedUser = User.builder().id(5).name("name").login("login").email("login@yandex.ru").birthday(LocalDate.of(1999, 12, 21)).build();

        User newUser = userStorage.save(user);
        Assertions.assertEquals(expectedUser, userStorage.getById(newUser.getId()));
    }

    @Test
    @Order(2)
    void updateTest() {
        User updatedUser = new User(1, LocalDate.of(1998, 11, 20), "new_name", "new_login", "new@yandex.ru");

        Assertions.assertEquals(userStorage.update(updatedUser), updatedUser);
    }

    @Test
    @Order(3)
    void getAllTest() {
        userStorage.save(new User(-1, LocalDate.of(1938, 10, 19), "rnd_name", "rnd_login", "rnd@yandex.ru"));
        Assertions.assertEquals(2, userStorage.getAll().size());
    }

    @Test
    @Order(4)
    void addFriendTest() {
        User user1 = new User(-1, LocalDate.of(1938, 10, 19), "rnd_name", "rnd_login", "rnd@yandex.ru");
        userStorage.save(user1);

        User user2 = new User(-1, LocalDate.of(1998, 11, 20), "new_name", "new_login", "new@yandex.ru");
        userStorage.save(user2);

        userStorage.addFriend(3, 4);
        Assertions.assertEquals(1, userStorage.getById(3).getFriends().size());
    }

    @Test
    @Order(5)
    void commonFriendsTest() {
        userStorage.addFriend(1, 4);
        Assertions.assertEquals(1, userStorage.getCommonFriends(1, 3).size());
    }

    @Test
    @Order(6)
    void deleteFriendTest() {
        userStorage.deleteFriend(1, 4);
        Assertions.assertEquals(0, userStorage.getFriends(1).size());
    }
}