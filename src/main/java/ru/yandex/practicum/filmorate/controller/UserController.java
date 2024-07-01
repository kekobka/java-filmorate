package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long seq = 0;

    private long generateId() {
        return ++seq;
    }

    private void validateUniqueness(User user) {
        for (User existedUser : users.values()) {
            if (user.getEmail().equals(existedUser.getEmail())) {
                throw new ValidationException("email уже используется.");
            }
        }
    }

    @GetMapping
    public Collection<User> get() {
        log.info("GET /users");
        return users.values();
    }

    private void addUser(User user) {
        validateUniqueness(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        try {
            log.info("POST /users => {}", user);
            addUser(user);
            return user;
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя.", e);
            throw e;
        }
    }

    @PutMapping
    public User update(@Validated @RequestBody User user) {
        try {
            log.info("PUT /users => ID: {}, {}", user.getId(), user);
            User oldUser = users.get(user.getId());
            updateUser(user, oldUser);
            return oldUser;
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя.", e);
            throw e;
        }
    }


    private void updateUser(User user, User oldUser) {
        if (user.getEmail() != null) {
            validateUniqueness(user);
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

}