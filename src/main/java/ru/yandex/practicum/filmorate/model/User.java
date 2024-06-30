package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "email")
public class User {
    private Long id;

    @NotBlank()
    @Email()
    private String email;

    @NotBlank()
    @Pattern(regexp = "^\\S+$")
    private String login;

    private String name;

    @Past()
    private LocalDate birthday;
}