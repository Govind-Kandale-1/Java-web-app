package com.example;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validUser_noViolations() {
        User user = new User("Alice Smith", "alice@example.com", "Engineer");
        assertThat(validator.validate(user)).isEmpty();
    }

    @Test
    void blankName_violation() {
        User user = new User("", "alice@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void nameTooShort_violation() {
        User user = new User("A", "alice@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void nameTooLong_violation() {
        User user = new User("A".repeat(51), "alice@example.com");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    void invalidEmail_violation() {
        User user = new User("Alice Smith", "not-an-email");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void blankEmail_violation() {
        User user = new User("Alice Smith", "");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void bioTooLong_violation() {
        User user = new User("Alice Smith", "alice@example.com", "x".repeat(501));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("bio"));
    }

    @Test
    void nullBio_noViolation() {
        User user = new User("Alice Smith", "alice@example.com");
        assertThat(validator.validate(user)).isEmpty();
    }
}
