package group5.sebm.BorrowRecordServiceTest;

import group5.sebm.BorrowRecord.controller.dto.*;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BorrowRecordDtoTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testDeviceReservationDtoValidation() {
        DeviceReservationDto dto = new DeviceReservationDto();
        dto.setUserId(null); // 应触发 NotNull
        dto.setDeviceId(1L);
        dto.setReserveStart(LocalDateTime.now().minusDays(1)); // 过去时间
        dto.setReserveEnd(LocalDateTime.now().plusDays(1));
        dto.setRemark("备注内容");

        Set<ConstraintViolation<DeviceReservationDto>> violations = validator.validate(dto);
        assertEquals(2, violations.size()); // userId null + reserveStart 非未来
    }

    @Test
    public void testBorrowRecordRenewDtoValidation() {
        BorrowRecordRenewDto dto = new BorrowRecordRenewDto();
        dto.setId(null); // NotNull
        dto.setUserId(null); // NotNull
        dto.setDueTime(new Date());

        Set<ConstraintViolation<BorrowRecordRenewDto>> violations = validator.validate(dto);
        assertEquals(2, violations.size()); // id + userId
    }

    @Test
    public void testBorrowRecordQueryDtoValidation() {
        BorrowRecordQueryDto dto = new BorrowRecordQueryDto();
        dto.setUserId(null); // NotNull
        dto.setPageNumber(1);
        dto.setPageSize(10);

        Set<ConstraintViolation<BorrowRecordQueryDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size()); // userId null
    }

    @Test
    public void testBorrowRecordQueryWithStatusDtoValidation() {
        BorrowRecordQueryWithStatusDto dto = new BorrowRecordQueryWithStatusDto();
        dto.setUserId(1L);
        dto.setPageNumber(1);
        dto.setPageSize(10);
        dto.setStatus(null); // NotNull

        Set<ConstraintViolation<BorrowRecordQueryWithStatusDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size()); // status null
    }

    @Test
    public void testDeviceReservationDtoRemarkSize() {
        DeviceReservationDto dto = new DeviceReservationDto();
        dto.setUserId(1L);
        dto.setDeviceId(1L);
        dto.setReserveStart(LocalDateTime.now().plusDays(1));
        dto.setReserveEnd(LocalDateTime.now().plusDays(2));
        dto.setRemark("a".repeat(201)); // 超过200字符

        Set<ConstraintViolation<DeviceReservationDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size()); // remark 长度超过200
    }
    @Test
    public void testValidDeviceReservationDto() {
        DeviceReservationDto dto = new DeviceReservationDto();
        dto.setUserId(1L);
        dto.setDeviceId(10L);
        dto.setReserveStart(LocalDateTime.now().plusHours(1));
        dto.setReserveEnd(LocalDateTime.now().plusHours(2));
        dto.setRemark("测试备注");

        Set<ConstraintViolation<DeviceReservationDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid DTO should have no violations");
    }

    @Test
    public void testInvalidDeviceReservationDto() {
        DeviceReservationDto dto = new DeviceReservationDto();
        dto.setUserId(null);  // 必填
        dto.setDeviceId(null); // 必填
        dto.setReserveStart(LocalDateTime.now().minusHours(1)); // 过去时间
        dto.setReserveEnd(LocalDateTime.now().minusHours(2));   // 过去时间
        dto.setRemark("a".repeat(201)); // 超过200字

        Set<ConstraintViolation<DeviceReservationDto>> violations = validator.validate(dto);
        assertEquals(5, violations.size()); // userId, deviceId, reserveStart, reserveEnd, remark
    }
    @Test
    public void testValidBorrowRecordRenewDto() {
        BorrowRecordRenewDto dto = new BorrowRecordRenewDto();
        dto.setId(1L);
        dto.setUserId(100L);
        dto.setDueTime(null); // 可选

        Set<ConstraintViolation<BorrowRecordRenewDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid DTO should have no violations");
    }

    @Test
    public void testInvalidBorrowRecordRenewDto() {
        BorrowRecordRenewDto dto = new BorrowRecordRenewDto();
        dto.setId(null); // 必填
        dto.setUserId(null); // 必填

        Set<ConstraintViolation<BorrowRecordRenewDto>> violations = validator.validate(dto);

        // 应该触发 id 和 userId 的 @NotNull 校验
        assertEquals(2, violations.size());
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));
    }
    @Test
    public void testValidDto() {
        DeviceReservationDto dto = new DeviceReservationDto();
        dto.setUserId(1L);
        dto.setDeviceId(100L);
        dto.setReserveStart(LocalDateTime.now().plusDays(1));
        dto.setReserveEnd(LocalDateTime.now().plusDays(2));
        dto.setRemark("这是一个测试备注");

        Set<ConstraintViolation<DeviceReservationDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "有效DTO不应该有验证错误");
    }

    @Test
    public void testNotNullValidation() {
        DeviceReservationDto dto = new DeviceReservationDto(); // 全部字段为空
        Set<ConstraintViolation<DeviceReservationDto>> violations = validator.validate(dto);
        assertEquals(4, violations.size(), "四个@NotNull字段应该触发验证错误");
    }

    @Test
    public void testFutureValidation() {
        DeviceReservationDto dto = new DeviceReservationDto();
        dto.setUserId(1L);
        dto.setDeviceId(100L);
        dto.setReserveStart(LocalDateTime.now().minusDays(1)); // 过去时间
        dto.setReserveEnd(LocalDateTime.now().minusDays(1)); // 过去时间

        Set<ConstraintViolation<DeviceReservationDto>> violations = validator.validate(dto);
        assertEquals(2, violations.size(), "开始时间和结束时间在过去，应该触发@Future验证错误");
    }

    @Test
    public void testRemarkSizeValidation() {
        DeviceReservationDto dto = new DeviceReservationDto();
        dto.setUserId(1L);
        dto.setDeviceId(100L);
        dto.setReserveStart(LocalDateTime.now().plusDays(1));
        dto.setReserveEnd(LocalDateTime.now().plusDays(2));
        dto.setRemark("a".repeat(201)); // 超过200字

        Set<ConstraintViolation<DeviceReservationDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "备注超过200字符应该触发@Size验证错误");
    }
}
