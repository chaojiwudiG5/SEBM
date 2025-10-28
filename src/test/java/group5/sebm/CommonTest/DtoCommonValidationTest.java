package group5.sebm.CommonTest;

import group5.sebm.common.dto.BorrowRecordDto;
import group5.sebm.common.dto.DeleteDto;
import group5.sebm.common.dto.DeviceDto;
import group5.sebm.common.dto.PageDto;
import group5.sebm.common.dto.UserDto;
import group5.sebm.common.dto.UserMaintenanceRecordDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DtoCommonValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== PageDto ====================
    @Test
    void testPageDto() {
        PageDto dto = new PageDto();
        dto.setPageNumber(1);
        dto.setPageSize(20);

        assertEquals(1, dto.getPageNumber());
        assertEquals(20, dto.getPageSize());

        PageDto dtoWithArgs = new PageDto(2, 50);
        assertEquals(2, dtoWithArgs.getPageNumber());
        assertEquals(50, dtoWithArgs.getPageSize());

        PageDto dtoBuilder = PageDto.builder()
            .pageNumber(3)
            .pageSize(100)
            .build();
        assertEquals(3, dtoBuilder.getPageNumber());
        assertEquals(100, dtoBuilder.getPageSize());

        // 校验约束（应无违规）
        Set<ConstraintViolation<PageDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());

        // 校验不合法数据
        PageDto invalidDto = new PageDto(null, 0);
        Set<ConstraintViolation<PageDto>> invalidViolations = validator.validate(invalidDto);
        assertFalse(invalidViolations.isEmpty());
    }

    // ==================== UserDto ====================
    @Test
    void testUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("Alice");
        dto.setEmail("alice@example.com");
        dto.setPhone("1234567890");
        dto.setGender(1);
        dto.setAvatarUrl("http://avatar.com/a.png");
        dto.setUserRole(1);
        dto.setUserStatus(0);
        dto.setAge(25);
        dto.setLevel(3);
        dto.setOverdueTimes(2);
        dto.setBorrowedDeviceCount(1);
        dto.setMaxBorrowedDeviceCount(3);
        dto.setMaxOverdueTimes(5);

        assertEquals(1L, dto.getId());
        assertEquals("Alice", dto.getUsername());
        assertEquals("alice@example.com", dto.getEmail());
        assertEquals("1234567890", dto.getPhone());
        assertEquals(1, dto.getGender());
        assertEquals("http://avatar.com/a.png", dto.getAvatarUrl());
        assertEquals(1, dto.getUserRole());
        assertEquals(0, dto.getUserStatus());
        assertEquals(25, dto.getAge());
        assertEquals(3, dto.getLevel());
        assertEquals(2, dto.getOverdueTimes());
        assertEquals(1, dto.getBorrowedDeviceCount());
        assertEquals(3, dto.getMaxBorrowedDeviceCount());
        assertEquals(5, dto.getMaxOverdueTimes());

        UserDto dtoWithArgs = new UserDto(
            1L, "Alice", "alice@example.com", "1234567890",
            1, "http://avatar.com/a.png", 1, 0,
            25, 3, 2, 1, 3, 5
        );
        assertEquals("Alice", dtoWithArgs.getUsername());

        UserDto dtoBuilder = UserDto.builder()
            .id(2L)
            .username("Bob")
            .email("bob@example.com")
            .build();
        assertEquals(2L, dtoBuilder.getId());
        assertEquals("Bob", dtoBuilder.getUsername());
        assertEquals("bob@example.com", dtoBuilder.getEmail());
    }

    // ==================== BorrowRecordDto ====================
    @Test
    void testBorrowRecordDto() {
        Date now = new Date();
        Date later = new Date(now.getTime() + 3600_000);

        BorrowRecordDto dto = new BorrowRecordDto();
        dto.setId(1L);
        dto.setUserId(10L);
        dto.setDeviceId(100L);
        dto.setBorrowTime(now);
        dto.setDueTime(later);
        dto.setReturnTime(null);
        dto.setStatus(0);
        dto.setRemarks("Normal borrow");

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getUserId());
        assertEquals(100L, dto.getDeviceId());
        assertEquals(now, dto.getBorrowTime());
        assertEquals(later, dto.getDueTime());
        assertNull(dto.getReturnTime());
        assertEquals(0, dto.getStatus());
        assertEquals("Normal borrow", dto.getRemarks());

        BorrowRecordDto dtoWithArgs = new BorrowRecordDto(1L, 10L, 100L, now, later, null, 0, "Normal borrow");
        assertEquals("Normal borrow", dtoWithArgs.getRemarks());
    }

    // ==================== DeleteDto ====================
    @Test
    void testDeleteDto() {
        DeleteDto dto = new DeleteDto();
        dto.setId(99L);
        assertEquals(99L, dto.getId());

        DeleteDto dtoWithArgs = new DeleteDto(88L);
        assertEquals(88L, dtoWithArgs.getId());

        // 校验合法
        Set<ConstraintViolation<DeleteDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());

        // 校验不合法
        DeleteDto invalid = new DeleteDto(null);
        Set<ConstraintViolation<DeleteDto>> invalidViolations = validator.validate(invalid);
        assertFalse(invalidViolations.isEmpty());
    }

    // ==================== DeviceDto ====================
    @Test
    void testDeviceDto() {
        DeviceDto dto = new DeviceDto();
        dto.setId(1L);
        dto.setDeviceName("Printer");
        dto.setDeviceType("Electronics");
        dto.setStatus(0);
        dto.setLocation("Lab A");
        dto.setDescription("A test printer");
        dto.setImage("http://image.com/p.png");

        assertEquals(1L, dto.getId());
        assertEquals("Printer", dto.getDeviceName());
        assertEquals("Electronics", dto.getDeviceType());
        assertEquals(0, dto.getStatus());
        assertEquals("Lab A", dto.getLocation());
        assertEquals("A test printer", dto.getDescription());
        assertEquals("http://image.com/p.png", dto.getImage());

        DeviceDto dtoWithArgs = new DeviceDto(1L, "Printer", "Electronics", 0, "Lab A", "A test printer", "http://image.com/p.png");
        assertEquals("Printer", dtoWithArgs.getDeviceName());
    }

    // ==================== UserMaintenanceRecordDto ====================
    @Test
    void testUserMaintenanceRecordDto() {
        Date create = new Date();
        Date update = new Date(create.getTime() + 1000);

        UserMaintenanceRecordDto dto = new UserMaintenanceRecordDto();
        dto.setId(1L);
        dto.setDeviceId(2L);
        dto.setUserId(3L);
        dto.setDescription("Fix issue");
        dto.setImage("http://image.com/fix.png");
        dto.setStatus(1);
        dto.setCreateTime(create);
        dto.setUpdateTime(update);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getDeviceId());
        assertEquals(3L, dto.getUserId());
        assertEquals("Fix issue", dto.getDescription());
        assertEquals("http://image.com/fix.png", dto.getImage());
        assertEquals(1, dto.getStatus());
        assertEquals(create, dto.getCreateTime());
        assertEquals(update, dto.getUpdateTime());

        UserMaintenanceRecordDto dtoWithArgs = new UserMaintenanceRecordDto(1L, 2L, 3L, "Fix issue", "http://image.com/fix.png", 1, create, update);
        assertEquals("Fix issue", dtoWithArgs.getDescription());
    }
}
