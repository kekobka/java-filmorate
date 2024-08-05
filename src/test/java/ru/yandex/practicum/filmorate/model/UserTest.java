package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserTest {
    private static final Validator validator;

    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.usingContext().getValidator();
        }
    }

    static User getUser() {
        return User.builder()
                .id(1)
                .login("login_test")
                .email("test@test.com")
                .name("test")
                .birthday(LocalDate.of(2024, 1, 1))
                .build();
    }

    @Test
    void shouldValidate() {
        User user = getUser();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldNotValidateEmailWithoutAt() {
        User user = getUser();
        user.setEmail("aaa.aaa1");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(Email.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void shouldNotValidateBlankEmail() {
        User user = getUser();
        user.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(NotBlank.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void shouldNotValidateEmailWithMisplacedAt() {
        User user = getUser();
        user.setEmail("@aaa.aaa1");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(Email.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    void shouldNotValidateBlankLogin() {
        User user = getUser();
        user.setLogin("");


        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldNotValidateLoginWithSpaces() {
        User user = getUser();
        user.setLogin("aa aa");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(Pattern.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("login", violation.getPropertyPath().toString());
    }


    @Test
    void shouldNotValidateBirthdayFromFuture() {
        User user = getUser();
        user.setBirthday(LocalDate.of(2124, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals(Past.class, violation.getConstraintDescriptor().getAnnotation().annotationType());
        assertEquals("birthday", violation.getPropertyPath().toString());
    }

}