package ru.yandex.practicum.filmorate.dao.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.FriendStatus;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper userRowMapper;
    private final FriendStatusRowMapper friendStatusRowMapper;

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper userRowMapper, FriendStatusRowMapper friendStatusRowMapper) {
        this.jdbc = jdbc;
        this.userRowMapper = userRowMapper;
        this.friendStatusRowMapper = friendStatusRowMapper;
    }

    private User getByIdInternal(long id) {
        try {
            User result = jdbc.queryForObject("SELECT * FROM users WHERE user_id = ?;", userRowMapper, id);
            List<Integer> friendsIds = jdbc.queryForList("SELECT other_user_id FROM friends WHERE user_id = ?;", Integer.class, id);
            assert result != null;
            result.setFriends(new HashSet<>(friendsIds));
            return result;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("user: %d not found", id));
        }
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.of(getByIdInternal(id));
    }

    @Override
    public boolean contains(int id) {
        return getById(id).isPresent();
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbc.query("SELECT * FROM users", userRowMapper);
        List<User> usersWithFriends = new ArrayList<>();
        for (User user : users) {
            List<Integer> list = new ArrayList<>();
            for (User user1 : getFriends(user.getId())) {
                Integer id = user1.getId();
                list.add(id);
            }
            user.setFriends(new HashSet<>(list));
            usersWithFriends.add(user);
        }
        return usersWithFriends;
    }

    @Override
    public User save(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc).withTableName("users").usingGeneratedKeyColumns("user_id");
        int id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        if (id == 0) {
            throw new InternalErrorException("Save failed");
        }
        user.setId(id);
        return user;
    }


    @Override
    public User update(User user) {
        try {
            getByIdInternal(user.getId());
            int updatedRow = jdbc.update("UPDATE users SET birthday = ?, name = ?, login = ?, email = ? WHERE user_id = ?;", user.getBirthday(), user.getName(), user.getLogin(), user.getEmail(), user.getId());
            if (updatedRow == 0) {
                throw new InternalErrorException("update failed");
            }

            return user;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("user: %d not found", user.getId()));
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        try {
            getByIdInternal(userId); // Ensure user exists
            getByIdInternal(friendId); // Ensure user exists
            jdbc.update("INSERT INTO friends (user_id, other_user_id) VALUES (?, ?)", userId, friendId);
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException("Add friend failed: " + e.getMessage());
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        try {
            getByIdInternal(userId); // Ensure user exists
            getByIdInternal(friendId); // Ensure user exists
            jdbc.update("DELETE FROM friends WHERE user_id = ? AND other_user_id = ?;", userId, friendId);
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException("add friend failed");
        }
    }

    private List<FriendStatus> getUserFriends(int userId) {
        try {
            return jdbc.query("SELECT * FROM friends WHERE user_id = ?;", friendStatusRowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new InternalErrorException("get friend failed");
        }
    }

    public List<User> getFriends(int userId) {
        getByIdInternal(userId);
        List<FriendStatus> friendsIds = getUserFriends(userId);
        return friendsIds.stream().map(friendStatus -> getByIdInternal(friendStatus.getFriendId())).toList();
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        List<User> userFriends = getUserFriends(userId).stream()
                .map(friendStatus -> getByIdInternal(friendStatus.getFriendId()))
                .toList();

        List<User> otherUserFriends = getUserFriends(otherUserId).stream()
                .map(friendStatus -> getByIdInternal(friendStatus.getFriendId()))
                .toList();

        return userFriends.stream()
                .distinct()
                .filter(otherUserFriends::contains)
                .toList();
    }
}
