package group5.sebm.Maintenance;

import group5.sebm.Maintenance.controller.dto.MechanicRecordQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanClaimDto;
import group5.sebm.Maintenance.controller.dto.MechanicanQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanUpdateDto;
import group5.sebm.Maintenance.controller.dto.UserCreateDto;
import group5.sebm.Maintenance.controller.dto.UserQueryDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== MechanicanQueryDto ====================
    @Test
    void testMechanicanQueryDtoValidation() {
        MechanicanQueryDto dto = new MechanicanQueryDto();
        dto.setPageNumber(1);
        dto.setPageSize(10);
        dto.setDeviceId(100L);
        dto.setStatus(2);

        Set<ConstraintViolation<MechanicanQueryDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());

        dto.setPageNumber(0); // 小于1
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // ==================== MechanicanClaimDto ====================
    @Test
    void testMechanicanClaimDtoValidation() {
        MechanicanClaimDto dto = new MechanicanClaimDto();
        dto.setUserMaintenanceRecordId(100L);

        Set<ConstraintViolation<MechanicanClaimDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());

        dto.setUserMaintenanceRecordId(null);
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // ==================== MechanicanUpdateDto ====================
    @Test
    void testMechanicanUpdateDtoValidation() {
        MechanicanUpdateDto dto = new MechanicanUpdateDto();
        dto.setId(1L);
        dto.setStatus(2);
        dto.setDescription("description");
        dto.setImage("http://image.url");
        dto.setUserMaintenanceRecordId(10L);

        Set<ConstraintViolation<MechanicanUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());

        dto.setStatus(1); // 小于2
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        dto.setStatus(4); // 大于3
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        dto.setDescription("x".repeat(501)); // 超长
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // ==================== MechanicRecordQueryDto ====================
    @Test
    void testMechanicRecordQueryDtoValidation() {
        MechanicRecordQueryDto dto = new MechanicRecordQueryDto();
        dto.setDeviceId(10L);
        dto.setStatus(1);

        Set<ConstraintViolation<MechanicRecordQueryDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());

        dto.setDeviceId(null);
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        dto.setDeviceId(10L);
        dto.setStatus(null);
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // ==================== UserCreateDto ====================
    @Test
    void testUserCreateDtoValidation() {
        UserCreateDto dto = new UserCreateDto();
        dto.setBorrowRecordId(1L);
        dto.setDescription("desc");
        dto.setImage("http://image.url");

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());

        dto.setDescription(""); // 为空
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        dto.setDescription("desc");
        dto.setImage(""); // 为空
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // ==================== UserQueryDto ====================
    @Test
    void testUserQueryDtoValidation() {
        UserQueryDto dto = new UserQueryDto();
        dto.setPageNumber(1);
        dto.setPageSize(10);

        Set<ConstraintViolation<UserQueryDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());

        dto.setPageNumber(0); // 不合法
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());

        dto.setPageNumber(1);
        dto.setPageSize(0); // 不合法
        violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
