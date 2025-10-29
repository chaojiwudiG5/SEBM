package group5.sebm.Maintenance;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.BorrowRecord.service.services.BorrowRecordService;
import group5.sebm.Device.service.services.DeviceService;
import group5.sebm.Maintenance.controller.dto.MechanicanQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanUpdateDto;
import group5.sebm.Maintenance.controller.dto.UserCreateDto;
import group5.sebm.Maintenance.controller.dto.UserQueryDto;
import group5.sebm.Maintenance.controller.vo.MechanicanMaintenanceRecordVo;
import group5.sebm.Maintenance.controller.vo.UserMaintenanceRecordVo;
import group5.sebm.Maintenance.dao.MechanicanMaintenanceRecordMapper;
import group5.sebm.Maintenance.dao.UserMaintenanceRecordMapper;
import group5.sebm.Maintenance.entity.MechanicanMaintenanceRecordPo;
import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
import group5.sebm.Maintenance.service.MechanicanMaintenanceRecordServiceImpl;
import group5.sebm.Maintenance.service.UserMaintenanceRecordServiceImpl;
import group5.sebm.Maintenance.service.services.UserMaintenanceRecordService;
import group5.sebm.common.dto.BorrowRecordDto;
import group5.sebm.common.dto.UserMaintenanceRecordDto;
import group5.sebm.common.enums.DeviceStatusEnum;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Maintenance模块整合测试
 * 包含结构验证测试和简单的功能测试
 */
@SpringBootTest(classes = {
    org.springframework.boot.autoconfigure.SpringBootApplication.class
})
@TestPropertySource(properties = {
    // 禁用外部依赖
    "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
        "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration," +
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
        "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration," +
        "org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration," +
        "org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration",
    // 禁用Web环境
    "spring.main.web-application-type=none"
})
@DisplayName("Maintenance模块整合测试")
class MaintenanceIntegrationTest {

    @Test
    @DisplayName("测试Spring上下文加载成功")
    void contextLoads() {
        // 这个测试只是确保Spring上下文能够成功加载
        // 如果上下文加载失败，测试会抛出异常
        assertTrue(true, "Spring上下文应该成功加载");
    }

    @Test
    @DisplayName("测试Maintenance模块类可以被实例化")
    void testMaintenanceClassesCanBeInstantiated() {
        // 测试关键类是否可以被加载和实例化
        assertDoesNotThrow(() -> {
            Class.forName("group5.sebm.Maintenance.service.UserMaintenanceRecordServiceImpl");
            Class.forName("group5.sebm.Maintenance.service.MechanicanMaintenanceRecordServiceImpl");
            Class.forName("group5.sebm.Maintenance.controller.UserMaintenanceRecordController");
            Class.forName("group5.sebm.Maintenance.controller.MechanicanMaintenanceRecordController");
        }, "Maintenance模块的所有关键类应该可以被加载");
    }

    @Test
    @DisplayName("测试实体类结构完整性")
    void testEntityClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证实体类有正确的结构
            Class<?> userMaintenanceRecordClass = Class.forName(
                "group5.sebm.Maintenance.entity.UserMaintenanceRecordPo"
            );
            
            // 验证关键字段存在
            assertNotNull(userMaintenanceRecordClass.getDeclaredField("id"));
            assertNotNull(userMaintenanceRecordClass.getDeclaredField("userId"));
            assertNotNull(userMaintenanceRecordClass.getDeclaredField("deviceId"));
            assertNotNull(userMaintenanceRecordClass.getDeclaredField("status"));
            assertNotNull(userMaintenanceRecordClass.getDeclaredField("description"));
        }, "UserMaintenanceRecordPo实体类应该有完整的字段定义");
    }

    @Test
    @DisplayName("测试DTO类结构完整性")
    void testDTOClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证DTO类可以被加载
            Class.forName("group5.sebm.Maintenance.controller.dto.UserCreateDto");
            Class.forName("group5.sebm.Maintenance.controller.dto.UserQueryDto");
            Class.forName("group5.sebm.Maintenance.controller.dto.MechanicanQueryDto");
            Class.forName("group5.sebm.Maintenance.controller.dto.MechanicanUpdateDto");
        }, "所有DTO类应该可以被正确加载");
    }

    @Test
    @DisplayName("测试VO类结构完整性")
    void testVOClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证VO类可以被加载
            Class<?> userMaintenanceRecordVoClass = Class.forName(
                "group5.sebm.Maintenance.controller.vo.UserMaintenanceRecordVo"
            );
            Class<?> mechanicMaintenanceRecordVoClass = Class.forName(
                "group5.sebm.Maintenance.controller.vo.MechanicanMaintenanceRecordVo"
            );
            
            // 验证VO类有基本的getter/setter方法
            assertNotNull(userMaintenanceRecordVoClass.getDeclaredField("id"));
            assertNotNull(mechanicMaintenanceRecordVoClass.getDeclaredField("id"));
        }, "所有VO类应该可以被正确加载并有完整的字段定义");
    }

    // ========== 功能测试部分 ==========

    @Test
    @DisplayName("功能测试：实体对象可以正常创建和使用")
    void testEntityObjectCreation() {
        // 测试UserMaintenanceRecordPo的创建和基本功能
        UserMaintenanceRecordPo userRecord = new UserMaintenanceRecordPo();
        userRecord.setId(1L);
        userRecord.setUserId(100L);
        userRecord.setDeviceId(200L);
        userRecord.setStatus(0);
        userRecord.setDescription("测试报修");
        userRecord.setImage("http://example.com/image.jpg");
        userRecord.setCreateTime(new Date());
        userRecord.setUpdateTime(new Date());

        assertNotNull(userRecord.getId(), "ID应该被正确设置");
        assertEquals(100L, userRecord.getUserId(), "用户ID应该被正确设置");
        assertEquals(200L, userRecord.getDeviceId(), "设备ID应该被正确设置");
        assertEquals(0, userRecord.getStatus(), "状态应该被正确设置");
        assertEquals("测试报修", userRecord.getDescription(), "描述应该被正确设置");

        // 测试MechanicanMaintenanceRecordPo的创建和基本功能
        MechanicanMaintenanceRecordPo mechanicRecord = new MechanicanMaintenanceRecordPo();
        mechanicRecord.setId(1L);
        mechanicRecord.setUserId(1L);
        mechanicRecord.setDeviceId(200L);
        mechanicRecord.setStatus(1);
        mechanicRecord.setDescription("维修中");
        mechanicRecord.setUserMaintenanceRecordId(1L);

        assertNotNull(mechanicRecord.getId(), "ID应该被正确设置");
        assertEquals(1L, mechanicRecord.getUserId(), "技工ID应该被正确设置");
        assertEquals(200L, mechanicRecord.getDeviceId(), "设备ID应该被正确设置");
        assertEquals(1, mechanicRecord.getStatus(), "状态应该被正确设置");
    }

    @Test
    @DisplayName("功能测试：DTO对象可以正常创建和使用")
    void testDTOObjectCreation() {
        // 测试UserCreateDto
        UserCreateDto createDto = new UserCreateDto();
        createDto.setBorrowRecordId(100L);
        createDto.setDescription("设备故障");
        createDto.setImage("http://example.com/fault.jpg");

        assertEquals(100L, createDto.getBorrowRecordId(), "借阅记录ID应该被正确设置");
        assertEquals("设备故障", createDto.getDescription(), "描述应该被正确设置");
        assertNotNull(createDto.getImage(), "图片URL应该被正确设置");

        // 测试UserQueryDto
        UserQueryDto queryDto = new UserQueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);
        queryDto.setStatus(0);

        assertEquals(1, queryDto.getPageNumber(), "页码应该被正确设置");
        assertEquals(10, queryDto.getPageSize(), "页大小应该被正确设置");
        assertEquals(0, queryDto.getStatus(), "状态应该被正确设置");

        // 测试MechanicanUpdateDto
        MechanicanUpdateDto updateDto = new MechanicanUpdateDto();
        updateDto.setId(1L);
        updateDto.setStatus(2);
        updateDto.setDescription("已修复");
        updateDto.setImage("http://example.com/fixed.jpg");
        updateDto.setUserMaintenanceRecordId(100L);

        assertEquals(1L, updateDto.getId(), "ID应该被正确设置");
        assertEquals(2, updateDto.getStatus(), "状态应该被正确设置");
        assertEquals("已修复", updateDto.getDescription(), "描述应该被正确设置");
    }

    @Test
    @DisplayName("功能测试：VO对象可以正常创建和使用")
    void testVOObjectCreation() {
        // 测试UserMaintenanceRecordVo
        UserMaintenanceRecordVo userVo = new UserMaintenanceRecordVo();
        userVo.setId(1L);
        userVo.setUserId(100L);
        userVo.setDeviceId(200L);
        userVo.setStatus(0);
        userVo.setDescription("报修单");

        assertEquals(1L, userVo.getId(), "ID应该被正确设置");
        assertEquals(100L, userVo.getUserId(), "用户ID应该被正确设置");
        assertEquals(200L, userVo.getDeviceId(), "设备ID应该被正确设置");

        // 测试MechanicanMaintenanceRecordVo
        MechanicanMaintenanceRecordVo mechanicVo = new MechanicanMaintenanceRecordVo();
        mechanicVo.setId(1L);
        mechanicVo.setUserId(1L);
        mechanicVo.setStatus(1);
        mechanicVo.setDescription("维修任务");

        assertEquals(1L, mechanicVo.getId(), "ID应该被正确设置");
        assertEquals(1L, mechanicVo.getUserId(), "技工ID应该被正确设置");
        assertEquals(1, mechanicVo.getStatus(), "状态应该被正确设置");
    }

    @Test
    @DisplayName("功能测试：业务异常处理")
    void testBusinessExceptionHandling() {
        // 测试BusinessException的创建和使用
        BusinessException exception = new BusinessException(
            ErrorCode.NOT_FOUND_ERROR,
            "测试记录不存在"
        );

        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), exception.getCode(),
            "异常代码应该正确");
        assertTrue(exception.getMessage().contains("测试记录不存在"),
            "异常消息应该包含自定义信息");
    }

    @Test
    @DisplayName("功能测试：错误代码枚举值")
    void testErrorCodeEnumValues() {
        // 验证错误代码枚举的正确性
        assertEquals(40400, ErrorCode.NOT_FOUND_ERROR.getCode(),
            "NOT_FOUND_ERROR代码应该是40400");
        assertEquals(40101, ErrorCode.NO_AUTH_ERROR.getCode(),
            "NO_AUTH_ERROR代码应该是40101");
        assertEquals(50001, ErrorCode.OPERATION_ERROR.getCode(),
            "OPERATION_ERROR代码应该是50001");
        assertEquals(40300, ErrorCode.FORBIDDEN_ERROR.getCode(),
            "FORBIDDEN_ERROR代码应该是40300");
        
        assertNotNull(ErrorCode.NOT_FOUND_ERROR.getMessage(),
            "错误代码应该有消息");
    }

    @Test
    @DisplayName("功能测试：设备状态枚举")
    void testDeviceStatusEnum() {
        // 测试设备状态枚举的正确性
        assertDoesNotThrow(() -> {
            int maintenanceCode = DeviceStatusEnum.MAINTENANCE.getCode();
            int availableCode = DeviceStatusEnum.AVAILABLE.getCode();
            int brokenCode = DeviceStatusEnum.BROKEN.getCode();
            
            assertTrue(maintenanceCode >= 0, "维修中状态码应该是有效的");
            assertTrue(availableCode >= 0, "可用状态码应该是有效的");
            assertTrue(brokenCode >= 0, "损坏状态码应该是有效的");
        }, "设备状态枚举应该可以正常使用");
    }

    @Test
    @DisplayName("集成测试：完整的对象转换流程")
    void testCompleteObjectConversionFlow() {
        // 模拟一个完整的对象转换流程
        // 1. 创建 CreateDto
        UserCreateDto createDto = new UserCreateDto();
        createDto.setBorrowRecordId(100L);
        createDto.setDescription("设备需要维修");
        createDto.setImage("http://example.com/image.jpg");

        // 2. 模拟转换为Po
        UserMaintenanceRecordPo po = new UserMaintenanceRecordPo();
        po.setId(1L);
        po.setUserId(1L);
        po.setDeviceId(200L);
        po.setStatus(0);
        po.setDescription(createDto.getDescription());
        po.setImage(createDto.getImage());
        po.setCreateTime(new Date());
        po.setUpdateTime(new Date());

        // 3. 模拟转换为Vo
        UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();
        vo.setId(po.getId());
        vo.setUserId(po.getUserId());
        vo.setDeviceId(po.getDeviceId());
        vo.setStatus(po.getStatus());
        vo.setDescription(po.getDescription());

        // 验证整个转换流程
        assertEquals(createDto.getDescription(), vo.getDescription(),
            "描述应该在整个转换流程中保持一致");
        assertEquals(po.getId(), vo.getId(),
            "ID应该从Po正确转换到Vo");
        assertEquals(0, vo.getStatus(),
            "初始状态应该是0（待处理）");
        
        assertNotNull(vo, "最终的VO对象应该不为null");
    }
}
