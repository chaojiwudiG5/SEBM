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

class CompleteDtoLombokAndValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ==================== 通用测试方法 ====================

    /**
     * 测试所有getter和setter方法
     */
    private <T> void testGettersAndSetters(T dto, Object[] values, String[] fieldNames) {
        try {
            for (int i = 0; i < values.length; i++) {
                String getterName = "get" + fieldNames[i].substring(0, 1).toUpperCase() + fieldNames[i].substring(1);
                String setterName = "set" + fieldNames[i].substring(0, 1).toUpperCase() + fieldNames[i].substring(1);

                // 测试setter
                dto.getClass().getMethod(setterName, values[i].getClass()).invoke(dto, values[i]);

                // 测试getter
                Object result = dto.getClass().getMethod(getterName).invoke(dto);
                assertEquals(values[i], result, "Getter/Setter for " + fieldNames[i] + " failed");
            }
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }

    /**
     * 测试toString包含所有字段
     */
    private <T> void testToStringContainsAllFields(T dto, String[] fieldNames) {
        String toString = dto.toString();
        for (String field : fieldNames) {
            assertTrue(toString.contains(field), "toString() should contain field: " + field);
        }
    }

    // ==================== MechanicanQueryDto ====================
    @Test
    void testMechanicanQueryDto_Complete() {
        // 测试数据
        MechanicanQueryDto dto = new MechanicanQueryDto();
        Object[] values = {1, 10, 100L, 2};
        String[] fields = {"pageNumber", "pageSize", "deviceId", "status"};

        // 测试getter和setter
        testGettersAndSetters(dto, values, fields);

        // 测试toString
        testToStringContainsAllFields(dto, fields);

        // 测试equals和hashCode
        MechanicanQueryDto dto1 = new MechanicanQueryDto();
        dto1.setPageNumber(1);
        dto1.setPageSize(10);
        dto1.setDeviceId(100L);
        dto1.setStatus(2);

        MechanicanQueryDto dto2 = new MechanicanQueryDto();
        dto2.setPageNumber(1);
        dto2.setPageSize(10);
        dto2.setDeviceId(100L);
        dto2.setStatus(2);

        // 自反性
        assertEquals(dto1, dto1);
        // 对称性
        assertEquals(dto1, dto2);
        assertEquals(dto2, dto1);
        // 传递性
        MechanicanQueryDto dto3 = new MechanicanQueryDto();
        dto3.setPageNumber(1);
        dto3.setPageSize(10);
        dto3.setDeviceId(100L);
        dto3.setStatus(2);
        assertEquals(dto1, dto3);
        // 一致性
        assertEquals(dto1.hashCode(), dto2.hashCode());
        // 非空性
        assertNotEquals(null, dto1);
        // 不同类型
        assertNotEquals(dto1, new Object());

        // 测试不同值
        MechanicanQueryDto dto4 = new MechanicanQueryDto();
        dto4.setPageNumber(2);
        assertNotEquals(dto1, dto4);

        // 测试null值
        dto1.setDeviceId(null);
        dto2.setDeviceId(null);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // 验证
        Set<ConstraintViolation<MechanicanQueryDto>> violations = validator.validate(dto1);
        assertTrue(violations.isEmpty());
    }

    // ==================== MechanicanClaimDto ====================
    @Test
    void testMechanicanClaimDto_Complete() {
        MechanicanClaimDto dto = new MechanicanClaimDto();
        Object[] values = {100L};
        String[] fields = {"userMaintenanceRecordId"};

        testGettersAndSetters(dto, values, fields);
        testToStringContainsAllFields(dto, fields);

        // equals和hashCode测试
        MechanicanClaimDto dto1 = new MechanicanClaimDto();
        dto1.setUserMaintenanceRecordId(100L);

        MechanicanClaimDto dto2 = new MechanicanClaimDto();
        dto2.setUserMaintenanceRecordId(100L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        MechanicanClaimDto dto3 = new MechanicanClaimDto();
        dto3.setUserMaintenanceRecordId(200L);
        assertNotEquals(dto1, dto3);

        // null值测试
        dto1.setUserMaintenanceRecordId(null);
        dto2.setUserMaintenanceRecordId(null);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // 验证
        Set<ConstraintViolation<MechanicanClaimDto>> violations = validator.validate(dto1);
        assertTrue(violations.isEmpty());
    }

    // ==================== MechanicanUpdateDto ====================
    @Test
    void testMechanicanUpdateDto_Complete() {
        MechanicanUpdateDto dto = new MechanicanUpdateDto();
        Object[] values = {1L, 2, "desc", "http://image.url", 10L};
        String[] fields = {"id", "status", "description", "image", "userMaintenanceRecordId"};

        testGettersAndSetters(dto, values, fields);
        testToStringContainsAllFields(dto, fields);

        // equals和hashCode测试
        MechanicanUpdateDto dto1 = new MechanicanUpdateDto();
        dto1.setId(1L);
        dto1.setStatus(2);
        dto1.setDescription("desc");
        dto1.setImage("http://image.url");
        dto1.setUserMaintenanceRecordId(10L);

        MechanicanUpdateDto dto2 = new MechanicanUpdateDto();
        dto2.setId(1L);
        dto2.setStatus(2);
        dto2.setDescription("desc");
        dto2.setImage("http://image.url");
        dto2.setUserMaintenanceRecordId(10L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // 测试单个字段不同
        MechanicanUpdateDto dto3 = new MechanicanUpdateDto();
        dto3.setId(2L); // 不同id
        dto3.setStatus(2);
        dto3.setDescription("desc");
        dto3.setImage("http://image.url");
        dto3.setUserMaintenanceRecordId(10L);
        assertNotEquals(dto1, dto3);

        // null值测试
        dto1.setDescription(null);
        dto2.setDescription(null);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // 验证
        Set<ConstraintViolation<MechanicanUpdateDto>> violations = validator.validate(dto1);
        assertTrue(violations.isEmpty());
    }

    // ==================== MechanicRecordQueryDto ====================
    @Test
    void testMechanicRecordQueryDto_Complete() {
        MechanicRecordQueryDto dto = new MechanicRecordQueryDto();
        Object[] values = {10L, 1};
        String[] fields = {"deviceId", "status"};

        testGettersAndSetters(dto, values, fields);
        testToStringContainsAllFields(dto, fields);

        // equals和hashCode测试
        MechanicRecordQueryDto dto1 = new MechanicRecordQueryDto();
        dto1.setDeviceId(10L);
        dto1.setStatus(1);

        MechanicRecordQueryDto dto2 = new MechanicRecordQueryDto();
        dto2.setDeviceId(10L);
        dto2.setStatus(1);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // 测试不同值
        MechanicRecordQueryDto dto3 = new MechanicRecordQueryDto();
        dto3.setDeviceId(20L);
        assertNotEquals(dto1, dto3);

        // null值测试
        dto1.setDeviceId(null);
        dto2.setDeviceId(null);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // 验证
        Set<ConstraintViolation<MechanicRecordQueryDto>> violations = validator.validate(dto1);
        assertTrue(violations.isEmpty());
    }

    // ==================== UserCreateDto ====================
    @Test
    void testUserCreateDto_Complete() {
        UserCreateDto dto = new UserCreateDto();
        Object[] values = {1L, "desc", "http://image.url"};
        String[] fields = {"borrowRecordId", "description", "image"};

        testGettersAndSetters(dto, values, fields);
        testToStringContainsAllFields(dto, fields);

        // equals和hashCode测试
        UserCreateDto dto1 = new UserCreateDto();
        dto1.setBorrowRecordId(1L);
        dto1.setDescription("desc");
        dto1.setImage("http://image.url");

        UserCreateDto dto2 = new UserCreateDto();
        dto2.setBorrowRecordId(1L);
        dto2.setDescription("desc");
        dto2.setImage("http://image.url");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // 测试不同值
        UserCreateDto dto3 = new UserCreateDto();
        dto3.setBorrowRecordId(2L);
        assertNotEquals(dto1, dto3);

        // null值测试
        dto1.setDescription(null);
        dto2.setDescription(null);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // 验证
        Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(dto1);
        assertTrue(violations.isEmpty());
    }

    // ==================== UserQueryDto ====================
    @Test
    void testUserQueryDto_Complete() {
        UserQueryDto dto = new UserQueryDto();
        Object[] values = {1, 10, 2};
        String[] fields = {"pageNumber", "pageSize", "status"};

        testGettersAndSetters(dto, values, fields);
        testToStringContainsAllFields(dto, fields);

        // equals和hashCode测试
        UserQueryDto dto1 = new UserQueryDto();
        dto1.setPageNumber(1);
        dto1.setPageSize(10);
        dto1.setStatus(2);

        UserQueryDto dto2 = new UserQueryDto();
        dto2.setPageNumber(1);
        dto2.setPageSize(10);
        dto2.setStatus(2);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        // 测试不同值
        UserQueryDto dto3 = new UserQueryDto();
        dto3.setPageNumber(2);
        assertNotEquals(dto1, dto3);

        // 验证
        Set<ConstraintViolation<UserQueryDto>> violations = validator.validate(dto1);
        assertTrue(violations.isEmpty());
    }
}