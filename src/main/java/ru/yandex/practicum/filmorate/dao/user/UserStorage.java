package ru.yandex.practicum.filmorate.dao.user;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Optional<User> getById(int id);

    boolean contains(int id);

    List<User> getAll();

    User save(User user);

    User update(User user);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int otherUserId);

}