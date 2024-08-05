package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public User save(User user) {
        log.info("POST /users => {}", user);
        return userStorage.save(user);
    }

    public List<User> getAll() {
        log.info("GET /users");
        return userStorage.getAll();
    }

    public User update(User user) {
        log.info("PUT /users => {}", user);
        if (!userStorage.contains(user.getId())) {
            throw new NotFoundException(String.format("user: %d not found", user.getId()));
        }
        return userStorage.update(user);
    }

    public void addFriend(long userId, long friendId) {
        log.info("PUT /users/{}/friends/{}", userId, friendId);

        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("user: %d not found", userId));
        }
        if (!userStorage.contains(friendId)) {
            throw new NotFoundException(String.format("user: %d not found", friendId));
        }
        userStorage.addFriend(userId, friendId);

    }

    public void deleteFriend(long userId, long friendId) {
        log.info("DELETE /users/{}/friends/{}", userId, friendId);
        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("user: %d not found", userId));
        }
        if (!userStorage.contains(friendId)) {
            throw new NotFoundException(String.format("user: %d not found", friendId));
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(long userId, long otherUserId) {
        log.info("GET /users/{}/friends/common/{}", userId, otherUserId);
        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("user: %d not found", userId));
        }
        if (!userStorage.contains(otherUserId)) {
            throw new NotFoundException(String.format("user: %d not found", otherUserId));
        }
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    public List<User> getFriends(Long userId) {
        log.info("GET /users/{}/friends", userId);
        if (!userStorage.contains(userId)) {
            throw new NotFoundException(String.format("user: %d not found", userId));
        }
        return userStorage.getFriends(userId);
    }
}
