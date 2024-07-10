package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.DateAfter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator implements ConstraintValidator<DateAfter, LocalDate> {
    private LocalDate minDate;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Override
    public void initialize(DateAfter constraintAnnotation) {
        String date = constraintAnnotation.value();
        try {
            minDate = LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected dd.MM.yyyy", e);
        }
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        return date.isAfter(minDate);
    }
}