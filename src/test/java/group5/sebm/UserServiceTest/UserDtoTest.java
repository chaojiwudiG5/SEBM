package group5.sebm.UserServiceTest;

import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.dto.UserDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserDtoTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testUserDtoLombok() {
        UserDto user = UserDto.builder()
                .id(1L)
                .username("tommy")
                .email("tommy@example.com")
                .phone("13812345678")
                .gender(1)
                .avatarUrl("http://avatar.com/tommy.png")
                .userRole(0)
                .userStatus(0)
                .age(25)
                .level(3)
                .overdueTimes(0)
                .borrowedDeviceCount(1)
                .maxBorrowedDeviceCount(3)
                .maxOverdueTimes(5)
                .build();

        assertEquals("tommy", user.getUsername());
        assertEquals(1L, user.getId());
        assertEquals(25, user.getAge());
    }

    @Test
    public void testPageDtoValidation() {
        PageDto validPage = new PageDto(1, 10);
        Set<ConstraintViolation<PageDto>> violations = validator.validate(validPage);
        assertTrue(violations.isEmpty());

        PageDto invalidPage = new PageDto(0, 0);
        violations = validator.validate(invalidPage);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testUpdateDtoValidation() {
        UpdateDto validUpdate = UpdateDto.builder()
                .id(1L)
                .username("tommy")
                .email("tommy@example.com")
                .phone("13812345678")
                .gender(1)
                .age(25)
                .build();

        Set<ConstraintViolation<UpdateDto>> violations = validator.validate(validUpdate);
        assertTrue(violations.isEmpty());

        UpdateDto invalidUpdate = UpdateDto.builder()
                .id(null)
                .username("")
                .email("invalid_email")
                .phone("123456")
                .gender(3)
                .age(-5)
                .build();

        violations = validator.validate(invalidUpdate);
        assertFalse(violations.isEmpty());
        assertEquals(6, violations.size()); // id, username, email, phone, gender, age
    }
}
