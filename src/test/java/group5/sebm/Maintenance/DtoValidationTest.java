package group5.sebm.Maintenance;

import group5.sebm.Maintenance.controller.dto.*;
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
    void testMechanicanQueryDto() {
        // 无参构造 + setter
        MechanicanQueryDto dto = new MechanicanQueryDto();
        dto.setPageNumber(1);
        dto.setPageSize(10);
        dto.setDeviceId(100L);
        dto.setStatus(2);

        assertEquals(1, dto.getPageNumber());
        assertEquals(10, dto.getPageSize());
        assertEquals(100L, dto.getDeviceId());
        assertEquals(2, dto.getStatus());
        assertTrue(dto.toString().contains("pageNumber"));

        // 有参构造
        MechanicanQueryDto dtoWithArgs = new MechanicanQueryDto(1, 10, 100L, 2);
        assertEquals(1, dtoWithArgs.getPageNumber());
        assertEquals(10, dtoWithArgs.getPageSize());
        assertEquals(100L, dtoWithArgs.getDeviceId());
        assertEquals(2, dtoWithArgs.getStatus());

        // Validation
        Set<ConstraintViolation<MechanicanQueryDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ==================== MechanicanClaimDto ====================
    @Test
    void testMechanicanClaimDto() {
        MechanicanClaimDto dto = new MechanicanClaimDto();
        dto.setUserMaintenanceRecordId(100L);
        assertEquals(100L, dto.getUserMaintenanceRecordId());
        assertTrue(dto.toString().contains("userMaintenanceRecordId"));

        MechanicanClaimDto dtoWithArgs = new MechanicanClaimDto(100L);
        assertEquals(100L, dtoWithArgs.getUserMaintenanceRecordId());

        Set<ConstraintViolation<MechanicanClaimDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ==================== MechanicanUpdateDto ====================
    @Test
    void testMechanicanUpdateDto() {
        MechanicanUpdateDto dto = new MechanicanUpdateDto();
        dto.setId(1L);
        dto.setStatus(2);
        dto.setDescription("desc");
        dto.setImage("http://image.url");
        dto.setUserMaintenanceRecordId(10L);

        assertEquals(1L, dto.getId());
        assertEquals(2, dto.getStatus());
        assertEquals("desc", dto.getDescription());
        assertEquals("http://image.url", dto.getImage());
        assertEquals(10L, dto.getUserMaintenanceRecordId());
        assertTrue(dto.toString().contains("id"));

        MechanicanUpdateDto dtoWithArgs = new MechanicanUpdateDto(1L, 2, "desc", "http://image.url", 10L);
        assertEquals(1L, dtoWithArgs.getId());
        assertEquals(2, dtoWithArgs.getStatus());
        assertEquals("desc", dtoWithArgs.getDescription());
        assertEquals("http://image.url", dtoWithArgs.getImage());
        assertEquals(10L, dtoWithArgs.getUserMaintenanceRecordId());

        Set<ConstraintViolation<MechanicanUpdateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ==================== MechanicRecordQueryDto ====================
    @Test
    void testMechanicRecordQueryDto() {
        MechanicRecordQueryDto dto = new MechanicRecordQueryDto();
        dto.setDeviceId(10L);
        dto.setStatus(1);

        assertEquals(10L, dto.getDeviceId());
        assertEquals(1, dto.getStatus());
        assertTrue(dto.toString().contains("deviceId"));

        MechanicRecordQueryDto dtoWithArgs = new MechanicRecordQueryDto(10L, 1);
        assertEquals(10L, dtoWithArgs.getDeviceId());
        assertEquals(1, dtoWithArgs.getStatus());

        Set<ConstraintViolation<MechanicRecordQueryDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ==================== UserCreateDto ====================
    @Test
    void testUserCreateDto() {
        UserCreateDto dto = new UserCreateDto();
        dto.setBorrowRecordId(1L);
        dto.setDescription("desc");
        dto.setImage("http://image.url");

        assertEquals(1L, dto.getBorrowRecordId());
        assertEquals("desc", dto.getDescription());
        assertEquals("http://image.url", dto.getImage());
        assertTrue(dto.toString().contains("borrowRecordId"));

        UserCreateDto dtoWithArgs = new UserCreateDto(1L, "desc", "http://image.url");
        assertEquals(1L, dtoWithArgs.getBorrowRecordId());
        assertEquals("desc", dtoWithArgs.getDescription());
        assertEquals("http://image.url", dtoWithArgs.getImage());

        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ==================== UserQueryDto ====================
    @Test
    void testUserQueryDto() {
        UserQueryDto dto = new UserQueryDto();
        dto.setPageNumber(1);
        dto.setPageSize(10);
        dto.setStatus(2);

        assertEquals(1, dto.getPageNumber());
        assertEquals(10, dto.getPageSize());
        assertEquals(2, dto.getStatus());
        assertTrue(dto.toString().contains("pageNumber"));

        UserQueryDto dtoWithArgs = new UserQueryDto(1, 10, 2);
        assertEquals(1, dtoWithArgs.getPageNumber());
        assertEquals(10, dtoWithArgs.getPageSize());
        assertEquals(2, dtoWithArgs.getStatus());

        Set<ConstraintViolation<UserQueryDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
