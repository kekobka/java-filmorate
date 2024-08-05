package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> userFriends = new HashMap<>();
    private long seq = 0;

    private long generateId() {
        return ++seq;
    }

    @Override
    public Optional<User> get(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean contains(long id) {
        return users.containsKey(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        if (users.get(user.getId()) == null) {
            user.setId(generateId());
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    private void updateUser(User oldUser, User user) {
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getLogin() != null) {
            oldUser.setLogin(user.getLogin());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            oldUser.setName(user.getName());
        }
        if (user.getBirthday() != null) {
            oldUser.setBirthday(user.getBirthday());
        }
    }

    @Override
    public User update(User user) {
        if (!contains(user.getId())) {
            throw new NotFoundException(String.format("user: %d not found", user.getId()));
        }
        updateUser(users.get(user.getId()), user);
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        Set<Long> user1Friends = userFriends.computeIfAbsent(userId, k -> new HashSet<>());
        user1Friends.add(friendId);

        Set<Long> user2Friends = userFriends.computeIfAbsent(friendId, k -> new HashSet<>());
        user2Friends.add(userId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("user %d not found", userId));
        }
        if (!users.containsKey(friendId)) {
            throw new NotFoundException(String.format("user %d not found", friendId));
        }
        if (!userFriends.containsKey(userId) || !userFriends.containsKey(friendId)) {
            return;
        }

        userFriends.get(userId).remove(friendId);
        userFriends.get(friendId).remove(userId);
    }

    @Override
    public List<User> getFriends(long userId) {
        if (!userFriends.containsKey(userId)) {
            return List.of();
        }

        return userFriends.get(userId).stream().map(users::get).toList();
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        Set<Long> userFriends = this.userFriends.getOrDefault(userId, new HashSet<>());
        Set<Long> otherUserFriends = this.userFriends.getOrDefault(otherUserId, new HashSet<>());
        Set<Long> commonFriends = new HashSet<>(userFriends);
        commonFriends.retainAll(otherUserFriends);
        return commonFriends.stream().map(users::get).toList();
    }
}