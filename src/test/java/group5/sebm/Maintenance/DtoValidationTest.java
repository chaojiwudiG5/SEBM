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

class DtoLombokAndValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== MechanicanQueryDto ====================
    @Test
    void testMechanicanQueryDto() {
        MechanicanQueryDto dto = new MechanicanQueryDto();
        dto.setPageNumber(1);
        dto.setPageSize(10);
        dto.setDeviceId(100L);
        dto.setStatus(2);

        // getter
        assertEquals(1, dto.getPageNumber());
        assertEquals(10, dto.getPageSize());
        assertEquals(100L, dto.getDeviceId());
        assertEquals(2, dto.getStatus());

        // toString
        assertTrue(dto.toString().contains("pageNumber"));

        // equals & hashCode
        MechanicanQueryDto dto2 = new MechanicanQueryDto();
        dto2.setPageNumber(1);
        dto2.setPageSize(10);
        dto2.setDeviceId(100L);
        dto2.setStatus(2);
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());

        // validation
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

        MechanicanClaimDto dto2 = new MechanicanClaimDto();
        dto2.setUserMaintenanceRecordId(100L);
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());

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

        MechanicanUpdateDto dto2 = new MechanicanUpdateDto();
        dto2.setId(1L);
        dto2.setStatus(2);
        dto2.setDescription("desc");
        dto2.setImage("http://image.url");
        dto2.setUserMaintenanceRecordId(10L);
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());

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

        MechanicRecordQueryDto dto2 = new MechanicRecordQueryDto();
        dto2.setDeviceId(10L);
        dto2.setStatus(1);
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());

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

        UserCreateDto dto2 = new UserCreateDto();
        dto2.setBorrowRecordId(1L);
        dto2.setDescription("desc");
        dto2.setImage("http://image.url");
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());

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

        UserQueryDto dto2 = new UserQueryDto();
        dto2.setPageNumber(1);
        dto2.setPageSize(10);
        dto2.setStatus(2);
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());

        Set<ConstraintViolation<UserQueryDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
