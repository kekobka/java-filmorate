package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User save(User user) {
        log.info("POST /users => {}", user);
        return userStorage.save(user);
    }

    public List<User> getAll() {
        log.info("GET /users");
        return userStorage.getAll();
    }

    public Optional<User> getById(int id) {
        log.info("GET /users/{}", id);
        return userStorage.getById(id);
    }

    public User update(User user) {
        log.info("PUT /users => {}", user);
        if (!userStorage.contains(user.getId())) {
            throw new NotFoundException(String.format("user: %d not found", user.getId()));
        }
        return userStorage.update(user);
    }

    public void addFriend(int userId, int friendId) {
        log.info("PUT /users/{}/friends/{}", userId, friendId);

        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("user: %d not found", userId));
        }
        if (!userStorage.contains(friendId)) {
            throw new NotFoundException(String.format("user: %d not found", friendId));
        }
        userStorage.addFriend(userId, friendId);

    }

    public void deleteFriend(int userId, int friendId) {
        log.info("DELETE /users/{}/friends/{}", userId, friendId);
        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("user: %d not found", userId));
        }
        if (!userStorage.contains(friendId)) {
            throw new NotFoundException(String.format("user: %d not found", friendId));
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        log.info("GET /users/{}/friends/common/{}", userId, otherUserId);
        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("user: %d not found", userId));
        }
        if (!userStorage.contains(otherUserId)) {
            throw new NotFoundException(String.format("user: %d not found", otherUserId));
        }
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public List<User> getFriends(Integer userId) {
        log.info("GET /users/{}/friends", userId);
        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("user: %d not found", userId));
        }
        return userStorage.getFriends(userId);
    }
}
