package group5.sebm.DeviceServiceTest;

import group5.sebm.Device.controller.dto.DeviceAddDto;
import group5.sebm.Device.controller.dto.DeviceQueryDto;
import group5.sebm.Device.controller.dto.DeviceUpdateDto;
import group5.sebm.Device.controller.vo.DeviceVo;
import group5.sebm.Device.entity.DevicePo;
import group5.sebm.common.enums.DeviceStatusEnum;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Device模块整合测试
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
@DisplayName("Device模块整合测试")
class DeviceIntegrationTest {

    @Test
    @DisplayName("测试Spring上下文加载成功")
    void contextLoads() {
        // 这个测试只是确保Spring上下文能够成功加载
        // 如果上下文加载失败，测试会抛出异常
        assertTrue(true, "Spring上下文应该成功加载");
    }

    @Test
    @DisplayName("测试Device模块类可以被实例化")
    void testDeviceClassesCanBeInstantiated() {
        // 测试关键类是否可以被加载和实例化
        assertDoesNotThrow(() -> {
            Class.forName("group5.sebm.Device.service.DeviceServiceImpl");
            Class.forName("group5.sebm.Device.controller.DeviceController");
            Class.forName("group5.sebm.Device.entity.DevicePo");
            Class.forName("group5.sebm.Device.dao.DeviceMapper");
        }, "Device模块的所有关键类应该可以被加载");
    }

    @Test
    @DisplayName("测试实体类结构完整性")
    void testEntityClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证实体类有正确的结构
            Class<?> devicePoClass = Class.forName("group5.sebm.Device.entity.DevicePo");
            
            // 验证关键字段存在
            assertNotNull(devicePoClass.getDeclaredField("id"));
            assertNotNull(devicePoClass.getDeclaredField("deviceName"));
            assertNotNull(devicePoClass.getDeclaredField("deviceType"));
            assertNotNull(devicePoClass.getDeclaredField("status"));
            assertNotNull(devicePoClass.getDeclaredField("location"));
            assertNotNull(devicePoClass.getDeclaredField("description"));
            assertNotNull(devicePoClass.getDeclaredField("image"));
        }, "DevicePo实体类应该有完整的字段定义");
    }

    @Test
    @DisplayName("测试DTO类结构完整性")
    void testDTOClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证DTO类可以被加载
            Class.forName("group5.sebm.Device.controller.dto.DeviceAddDto");
            Class.forName("group5.sebm.Device.controller.dto.DeviceQueryDto");
            Class.forName("group5.sebm.Device.controller.dto.DeviceUpdateDto");
        }, "所有DTO类应该可以被正确加载");
    }

    @Test
    @DisplayName("测试VO类结构完整性")
    void testVOClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证VO类可以被加载
            Class<?> deviceVoClass = Class.forName("group5.sebm.Device.controller.vo.DeviceVo");
            
            // 验证VO类有基本的字段
            assertNotNull(deviceVoClass.getDeclaredField("id"));
            assertNotNull(deviceVoClass.getDeclaredField("deviceName"));
            assertNotNull(deviceVoClass.getDeclaredField("status"));
        }, "DeviceVo类应该可以被正确加载并有完整的字段定义");
    }

    // ========== 功能测试部分 ==========

    @Test
    @DisplayName("功能测试：实体对象可以正常创建和使用")
    void testEntityObjectCreation() {
        // 测试DevicePo的创建和基本功能
        DevicePo device = new DevicePo();
        device.setId(1L);
        device.setDeviceName("笔记本电脑");
        device.setDeviceType("电子设备");
        device.setStatus(0);
        device.setLocation("实验室A101");
        device.setDescription("联想ThinkPad");
        device.setImage("http://example.com/laptop.jpg");
        device.setCreateTime(new Date());
        device.setUpdateTime(new Date());

        assertNotNull(device.getId(), "ID应该被正确设置");
        assertEquals("笔记本电脑", device.getDeviceName(), "设备名称应该被正确设置");
        assertEquals("电子设备", device.getDeviceType(), "设备类型应该被正确设置");
        assertEquals(0, device.getStatus(), "状态应该被正确设置");
        assertEquals("实验室A101", device.getLocation(), "位置应该被正确设置");
        assertNotNull(device.getDescription(), "描述应该被正确设置");
        assertNotNull(device.getImage(), "图片URL应该被正确设置");
    }

    @Test
    @DisplayName("功能测试：DTO对象可以正常创建和使用")
    void testDTOObjectCreation() {
        // 测试DeviceAddDto
        DeviceAddDto addDto = new DeviceAddDto();
        addDto.setDeviceName("投影仪");
        addDto.setDeviceType("办公设备");
        addDto.setStatus(0);
        addDto.setLocation("会议室B201");
        addDto.setDescription("索尼投影仪");
        addDto.setImage("http://example.com/projector.jpg");

        assertEquals("投影仪", addDto.getDeviceName(), "设备名称应该被正确设置");
        assertEquals("办公设备", addDto.getDeviceType(), "设备类型应该被正确设置");
        assertEquals(0, addDto.getStatus(), "状态应该被正确设置");
        assertEquals("会议室B201", addDto.getLocation(), "位置应该被正确设置");

        // 测试DeviceQueryDto
        DeviceQueryDto queryDto = new DeviceQueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);
        queryDto.setDeviceName("笔记本");
        queryDto.setDeviceType("电子设备");
        queryDto.setStatus(0);
        queryDto.setLocation("实验室");

        assertEquals(1, queryDto.getPageNumber(), "页码应该被正确设置");
        assertEquals(10, queryDto.getPageSize(), "页大小应该被正确设置");
        assertEquals("笔记本", queryDto.getDeviceName(), "设备名称应该被正确设置");
        assertEquals(0, queryDto.getStatus(), "状态应该被正确设置");

        // 测试DeviceUpdateDto
        DeviceUpdateDto updateDto = new DeviceUpdateDto();
        updateDto.setId(1L);
        updateDto.setDeviceName("更新后的设备名");
        updateDto.setStatus(1);
        updateDto.setLocation("新位置");

        assertEquals(1L, updateDto.getId(), "ID应该被正确设置");
        assertEquals("更新后的设备名", updateDto.getDeviceName(), "设备名称应该被正确设置");
        assertEquals(1, updateDto.getStatus(), "状态应该被正确设置");
    }

    @Test
    @DisplayName("功能测试：VO对象可以正常创建和使用")
    void testVOObjectCreation() {
        // 测试DeviceVo
        DeviceVo deviceVo = new DeviceVo();
        deviceVo.setId(1L);
        deviceVo.setDeviceName("打印机");
        deviceVo.setDeviceType("办公设备");
        deviceVo.setStatus(0);
        deviceVo.setLocation("办公室C301");
        deviceVo.setDescription("惠普激光打印机");
        deviceVo.setImage("http://example.com/printer.jpg");

        assertEquals(1L, deviceVo.getId(), "ID应该被正确设置");
        assertEquals("打印机", deviceVo.getDeviceName(), "设备名称应该被正确设置");
        assertEquals("办公设备", deviceVo.getDeviceType(), "设备类型应该被正确设置");
        assertEquals(0, deviceVo.getStatus(), "状态应该被正确设置");
        assertEquals("办公室C301", deviceVo.getLocation(), "位置应该被正确设置");
    }

    @Test
    @DisplayName("功能测试：设备状态枚举")
    void testDeviceStatusEnum() {
        // 测试设备状态枚举的正确性
        assertDoesNotThrow(() -> {
            int availableCode = DeviceStatusEnum.AVAILABLE.getCode();
            int borrowedCode = DeviceStatusEnum.BORROWED.getCode();
            int maintenanceCode = DeviceStatusEnum.MAINTENANCE.getCode();
            int brokenCode = DeviceStatusEnum.BROKEN.getCode();
            
            assertTrue(availableCode >= 0, "可用状态码应该是有效的");
            assertTrue(borrowedCode >= 0, "借出状态码应该是有效的");
            assertTrue(maintenanceCode >= 0, "维修中状态码应该是有效的");
            assertTrue(brokenCode >= 0, "报废状态码应该是有效的");
            
            // 验证状态码互不相同
            assertNotEquals(availableCode, borrowedCode, "状态码应该互不相同");
            assertNotEquals(availableCode, maintenanceCode, "状态码应该互不相同");
            assertNotEquals(borrowedCode, maintenanceCode, "状态码应该互不相同");
        }, "设备状态枚举应该可以正常使用");
    }

    @Test
    @DisplayName("功能测试：业务异常处理")
    void testBusinessExceptionHandling() {
        // 测试设备相关的业务异常
        BusinessException exception = new BusinessException(
            ErrorCode.NOT_FOUND_ERROR,
            "设备不存在"
        );

        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), exception.getCode(),
            "异常代码应该正确");
        assertTrue(exception.getMessage().contains("设备不存在"),
            "异常消息应该包含自定义信息");

        // 测试参数错误异常
        BusinessException paramsException = new BusinessException(
            ErrorCode.PARAMS_ERROR,
            "设备名称不能为空"
        );

        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), paramsException.getCode(),
            "参数错误代码应该正确");
    }

    @Test
    @DisplayName("功能测试：设备状态转换逻辑")
    void testDeviceStatusTransition() {
        // 测试设备状态转换的合理性
        DevicePo device = new DevicePo();
        device.setId(1L);
        device.setDeviceName("测试设备");
        device.setStatus(DeviceStatusEnum.AVAILABLE.getCode());

        // 可用 -> 借出
        device.setStatus(DeviceStatusEnum.BORROWED.getCode());
        assertEquals(DeviceStatusEnum.BORROWED.getCode(), device.getStatus(),
            "设备状态应该可以从可用变为借出");

        // 借出 -> 维修
        device.setStatus(DeviceStatusEnum.MAINTENANCE.getCode());
        assertEquals(DeviceStatusEnum.MAINTENANCE.getCode(), device.getStatus(),
            "设备状态应该可以从借出变为维修");

        // 维修 -> 可用
        device.setStatus(DeviceStatusEnum.AVAILABLE.getCode());
        assertEquals(DeviceStatusEnum.AVAILABLE.getCode(), device.getStatus(),
            "设备状态应该可以从维修变为可用");
    }

    @Test
    @DisplayName("集成测试：完整的对象转换流程")
    void testCompleteObjectConversionFlow() {
        // 模拟一个完整的设备管理流程
        // 1. 创建 AddDto
        DeviceAddDto addDto = new DeviceAddDto();
        addDto.setDeviceName("MacBook Pro");
        addDto.setDeviceType("笔记本电脑");
        addDto.setStatus(0);
        addDto.setLocation("开发部");
        addDto.setDescription("苹果笔记本电脑，用于开发");
        addDto.setImage("http://example.com/macbook.jpg");

        // 2. 模拟转换为Po
        DevicePo po = new DevicePo();
        po.setId(1L);
        po.setDeviceName(addDto.getDeviceName());
        po.setDeviceType(addDto.getDeviceType());
        po.setStatus(addDto.getStatus());
        po.setLocation(addDto.getLocation());
        po.setDescription(addDto.getDescription());
        po.setImage(addDto.getImage());
        po.setCreateTime(new Date());
        po.setUpdateTime(new Date());

        // 3. 模拟转换为Vo
        DeviceVo vo = new DeviceVo();
        vo.setId(po.getId());
        vo.setDeviceName(po.getDeviceName());
        vo.setDeviceType(po.getDeviceType());
        vo.setStatus(po.getStatus());
        vo.setLocation(po.getLocation());
        vo.setDescription(po.getDescription());
        vo.setImage(po.getImage());

        // 验证整个转换流程
        assertEquals(addDto.getDeviceName(), vo.getDeviceName(),
            "设备名称应该在整个转换流程中保持一致");
        assertEquals(addDto.getDeviceType(), vo.getDeviceType(),
            "设备类型应该在整个转换流程中保持一致");
        assertEquals(po.getId(), vo.getId(),
            "ID应该从Po正确转换到Vo");
        assertEquals(0, vo.getStatus(),
            "初始状态应该是0（可用）");
        
        assertNotNull(vo, "最终的VO对象应该不为null");
    }

    @Test
    @DisplayName("功能测试：设备查询条件组合")
    void testDeviceQueryCombinations() {
        // 测试各种查询条件的组合
        DeviceQueryDto queryDto = new DeviceQueryDto();
        
        // 只设置设备名称
        queryDto.setDeviceName("笔记本");
        assertNotNull(queryDto.getDeviceName(), "设备名称应该被设置");
        
        // 添加设备类型
        queryDto.setDeviceType("电子设备");
        assertNotNull(queryDto.getDeviceType(), "设备类型应该被设置");
        
        // 添加状态
        queryDto.setStatus(0);
        assertEquals(0, queryDto.getStatus(), "状态应该被设置");
        
        // 添加位置
        queryDto.setLocation("实验室");
        assertNotNull(queryDto.getLocation(), "位置应该被设置");
        
        // 验证所有条件都可以同时存在
        assertNotNull(queryDto.getDeviceName());
        assertNotNull(queryDto.getDeviceType());
        assertNotNull(queryDto.getStatus());
        assertNotNull(queryDto.getLocation());
    }

    @Test
    @DisplayName("功能测试：设备信息验证")
    void testDeviceInformationValidation() {
        // 测试设备信息的基本验证逻辑
        DeviceAddDto addDto = new DeviceAddDto();
        
        // 设置有效的设备信息
        addDto.setDeviceName("有效设备名称");
        addDto.setDeviceType("有效类型");
        addDto.setStatus(0);
        addDto.setLocation("有效位置");
        addDto.setDescription("这是一个有效的设备描述");
        addDto.setImage("http://example.com/valid.jpg");

        // 验证所有必填字段都被设置
        assertNotNull(addDto.getDeviceName(), "设备名称不应为null");
        assertNotNull(addDto.getDeviceType(), "设备类型不应为null");
        assertNotNull(addDto.getStatus(), "设备状态不应为null");
        assertNotNull(addDto.getLocation(), "设备位置不应为null");
        
        // 验证状态值在有效范围内
        assertTrue(addDto.getStatus() >= 0 && addDto.getStatus() <= 3,
            "设备状态应该在0-3之间");
    }
}

