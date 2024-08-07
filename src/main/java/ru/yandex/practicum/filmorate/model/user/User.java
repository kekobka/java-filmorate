package ru.yandex.practicum.filmorate.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Data
@EqualsAndHashCode(of = "email")
public class User {
    private Integer id;

    @NotBlank()
    @Email()
    private String email;

    @NotBlank()
    @Pattern(regexp = "^\\S+$")
    private String login;

    private String name;

    @Past
    private LocalDate birthday;

    private Set<Integer> friends = new HashSet<>();


    @Builder
    public User(int id, LocalDate birthday, String name, String login, String email) {
        this.id = id;
        this.birthday = birthday;
        this.name = name;
        this.login = login;
        this.email = email;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("login", login);
        values.put("name", name);
        values.put("email", email);
        values.put("birthday", birthday);
        return values;
    }
}