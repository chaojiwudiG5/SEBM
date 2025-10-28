package group5.sebm.Maintenance.Integration;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import group5.sebm.Device.dao.DeviceMapper;
import group5.sebm.Device.entity.DevicePo;
import group5.sebm.Maintenance.controller.dto.MechanicanQueryDto;
import group5.sebm.Maintenance.controller.dto.MechanicanUpdateDto;
import group5.sebm.Maintenance.controller.dto.MechanicRecordQueryDto;
import group5.sebm.Maintenance.dao.MechanicanMaintenanceRecordMapper;
import group5.sebm.Maintenance.dao.UserMaintenanceRecordMapper;
import group5.sebm.Maintenance.entity.MechanicanMaintenanceRecordPo;
import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
import group5.sebm.User.dao.UserMapper;
import group5.sebm.User.entity.UserPo;
import group5.sebm.common.enums.DeviceStatusEnum;
import group5.sebm.common.enums.UserRoleEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 技工维修记录集成测试
 * 测试完整的API流程，包括数据库交互
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(IntegrationTestConfig.class)
//@TestPropertySource(properties = {
//    "spring.rabbitmq.host=hopper.proxy.rlwy.net",
//    "spring.rabbitmq.port=50343",
//    "spring.rabbitmq.username=test",
//    "spring.rabbitmq.password=test"
//})
public class MechanicanMaintenanceRecordIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MechanicanMaintenanceRecordMapper mechanicanMaintenanceRecordMapper;

    @Autowired
    private UserMaintenanceRecordMapper userMaintenanceRecordMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private UserMapper userMapper;

    private Long testMechanicId;
    private Long testDeviceId;
    private Long testUserMaintenanceRecordId;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        // 创建测试技工用户
        UserPo mechanic = new UserPo();
        mechanic.setUsername("testmechanic");
        mechanic.setPassword("password123");
        mechanic.setEmail("mechanic@example.com");
        mechanic.setUserRole(UserRoleEnum.TECHNICIAN.getCode());
        mechanic.setCreateTime(new Date());
        mechanic.setUpdateTime(new Date());
        userMapper.insert(mechanic);
        testMechanicId = mechanic.getId();

        // 创建测试普通用户
        UserPo user = new UserPo();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("user@example.com");
        user.setUserRole(UserRoleEnum.USER.getCode());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insert(user);
        testUserId = user.getId();

        // 创建测试设备
        DevicePo testDevice = new DevicePo();
        testDevice.setDeviceName("Test Maintenance Device");
        testDevice.setDeviceType("Server");
        testDevice.setStatus(DeviceStatusEnum.MAINTENANCE.getCode());
        testDevice.setLocation("Server Room");
        testDevice.setCreateTime(new Date());
        testDevice.setUpdateTime(new Date());
        deviceMapper.insert(testDevice);
        testDeviceId = testDevice.getId();

        // 创建测试用户维修记录
        UserMaintenanceRecordPo userRecord = new UserMaintenanceRecordPo();
        userRecord.setUserId(testUserId);
        userRecord.setDeviceId(testDeviceId);
        userRecord.setDescription("设备需要维修");
        userRecord.setStatus(0);
        userRecord.setCreateTime(new Date());
        userRecord.setUpdateTime(new Date());
        userMaintenanceRecordMapper.insert(userRecord);
        testUserMaintenanceRecordId = userRecord.getId();
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        QueryWrapper<MechanicanMaintenanceRecordPo> mechanicWrapper = new QueryWrapper<>();
        mechanicWrapper.eq("userId", testMechanicId);
        mechanicanMaintenanceRecordMapper.delete(mechanicWrapper);

        if (testUserMaintenanceRecordId != null) {
            userMaintenanceRecordMapper.deleteById(testUserMaintenanceRecordId);
        }
        if (testDeviceId != null) {
            deviceMapper.deleteById(testDeviceId);
        }
        if (testMechanicId != null) {
            userMapper.deleteById(testMechanicId);
        }
        if (testUserId != null) {
            userMapper.deleteById(testUserId);
        }
    }

    @Test
    @Transactional
    void testAddMaintenanceTask_Success() throws Exception {
        // 发送添加维修任务请求
        mockMvc.perform(post("/mechanicanMaintenanceRecord/add")
                .param("userMaintenanceRecordId", testUserMaintenanceRecordId.toString())
                .param("mechanicId", testMechanicId.toString())
                .requestAttr("role", UserRoleEnum.ADMIN.getCode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isNumber());

        // 验证数据库中的数据
        QueryWrapper<MechanicanMaintenanceRecordPo> wrapper = new QueryWrapper<>();
        wrapper.eq("userId", testMechanicId)
               .eq("deviceId", testDeviceId);
        MechanicanMaintenanceRecordPo record = mechanicanMaintenanceRecordMapper.selectOne(wrapper);
        assertNotNull(record);
        assertEquals(testMechanicId, record.getUserId());
        assertEquals(testDeviceId, record.getDeviceId());
    }

    @Test
    @Transactional
    void testListMyTasks() throws Exception {
        // 先创建一个技工维修记录
        MechanicanMaintenanceRecordPo record = new MechanicanMaintenanceRecordPo();
        record.setUserId(testMechanicId);
        record.setDeviceId(testDeviceId);
        record.setDescription("维修任务测试");
        record.setStatus(1);
        record.setUserMaintenanceRecordId(testUserMaintenanceRecordId);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        mechanicanMaintenanceRecordMapper.insert(record);

        // 准备查询参数
        MechanicanQueryDto queryDto = new MechanicanQueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);

        // 发送查询请求
        mockMvc.perform(post("/mechanicanMaintenanceRecord/myList")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", testMechanicId)
                .requestAttr("role", UserRoleEnum.TECHNICIAN.getCode())
                .content(objectMapper.writeValueAsString(queryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.records[0].userId").value(testMechanicId));
    }

    @Test
    @Transactional
    void testGetRecordDetail_Success() throws Exception {
        // 创建一个技工维修记录
        MechanicanMaintenanceRecordPo record = new MechanicanMaintenanceRecordPo();
        record.setUserId(testMechanicId);
        record.setDeviceId(testDeviceId);
        record.setDescription("详细维修记录");
        record.setStatus(1);
        record.setUserMaintenanceRecordId(testUserMaintenanceRecordId);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        mechanicanMaintenanceRecordMapper.insert(record);

        // 准备查询参数
        MechanicRecordQueryDto queryDto = new MechanicRecordQueryDto();
        queryDto.setDeviceId(testDeviceId);
        queryDto.setStatus(1);

        // 发送查询详情请求
        mockMvc.perform(post("/mechanicanMaintenanceRecord/getRecordDetail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.deviceId").value(testDeviceId))
                .andExpect(jsonPath("$.data.description").value("详细维修记录"));
    }

    @Test
    @Transactional
    void testUpdateTaskStatus_ToFixed() throws Exception {
        // 创建一个技工维修记录
        MechanicanMaintenanceRecordPo record = new MechanicanMaintenanceRecordPo();
        record.setUserId(testMechanicId);
        record.setDeviceId(testDeviceId);
        record.setDescription("待更新的维修记录");
        record.setStatus(1);  // 处理中
        record.setUserMaintenanceRecordId(testUserMaintenanceRecordId);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        mechanicanMaintenanceRecordMapper.insert(record);
        Long recordId = record.getId();

        // 准备更新数据
        MechanicanUpdateDto updateDto = new MechanicanUpdateDto();
        updateDto.setId(recordId);
        updateDto.setStatus(2);  // 已修复
        updateDto.setDescription("维修完成");
        updateDto.setImage("http://example.com/fixed.jpg");
        updateDto.setUserMaintenanceRecordId(testUserMaintenanceRecordId);

        // 发送更新请求
        mockMvc.perform(post("/mechanicanMaintenanceRecord/updateStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", testMechanicId)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));

        // 验证数据库中的状态已更新
        MechanicanMaintenanceRecordPo updatedRecord = mechanicanMaintenanceRecordMapper.selectById(recordId);
        assertEquals(2, updatedRecord.getStatus());
        assertEquals("维修完成", updatedRecord.getDescription());

        // 验证设备状态已更新为可用
        DevicePo device = deviceMapper.selectById(testDeviceId);
        assertEquals(DeviceStatusEnum.AVAILABLE.getCode(), device.getStatus());
    }

    @Test
    @Transactional
    void testUpdateTaskStatus_ToBroken() throws Exception {
        // 创建一个技工维修记录
        MechanicanMaintenanceRecordPo record = new MechanicanMaintenanceRecordPo();
        record.setUserId(testMechanicId);
        record.setDeviceId(testDeviceId);
        record.setDescription("无法修复的设备");
        record.setStatus(1);
        record.setUserMaintenanceRecordId(testUserMaintenanceRecordId);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        mechanicanMaintenanceRecordMapper.insert(record);
        Long recordId = record.getId();

        // 准备更新数据为无法修复
        MechanicanUpdateDto updateDto = new MechanicanUpdateDto();
        updateDto.setId(recordId);
        updateDto.setStatus(3);  // 无法修复
        updateDto.setDescription("设备损坏严重，无法修复");
        updateDto.setUserMaintenanceRecordId(testUserMaintenanceRecordId);

        // 发送更新请求
        mockMvc.perform(post("/mechanicanMaintenanceRecord/updateStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", testMechanicId)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(true));

        // 验证设备状态已更新为损坏
        DevicePo device = deviceMapper.selectById(testDeviceId);
        assertEquals(DeviceStatusEnum.BROKEN.getCode(), device.getStatus());
    }

    @Test
    @Transactional
    void testUpdateTaskStatus_Unauthorized() throws Exception {
        // 创建一个属于其他技工的维修记录
        Long otherMechanicId = testMechanicId + 1000;
        
        MechanicanMaintenanceRecordPo record = new MechanicanMaintenanceRecordPo();
        record.setUserId(otherMechanicId);
        record.setDeviceId(testDeviceId);
        record.setDescription("其他技工的记录");
        record.setStatus(1);
        record.setUserMaintenanceRecordId(testUserMaintenanceRecordId);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        mechanicanMaintenanceRecordMapper.insert(record);
        Long recordId = record.getId();

        // 尝试用当前技工ID更新
        MechanicanUpdateDto updateDto = new MechanicanUpdateDto();
        updateDto.setId(recordId);
        updateDto.setStatus(2);
        updateDto.setUserMaintenanceRecordId(testUserMaintenanceRecordId);

        // 发送更新请求，预期失败
        mockMvc.perform(post("/mechanicanMaintenanceRecord/updateStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", testMechanicId)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(greaterThan(0)));
    }

    @Test
    @Transactional
    void testListMyTasksWithFilters() throws Exception {
        // 创建多个不同状态的维修记录
        for (int i = 0; i < 3; i++) {
            MechanicanMaintenanceRecordPo record = new MechanicanMaintenanceRecordPo();
            record.setUserId(testMechanicId);
            record.setDeviceId(testDeviceId);
            record.setDescription("维修记录 " + i);
            record.setStatus(i % 2 + 1);  // 状态轮换 1, 2
            record.setUserMaintenanceRecordId(testUserMaintenanceRecordId);
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            mechanicanMaintenanceRecordMapper.insert(record);
        }

        // 准备查询参数，过滤状态为1的记录
        MechanicanQueryDto queryDto = new MechanicanQueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);
        queryDto.setStatus(1);

        // 发送查询请求
        mockMvc.perform(post("/mechanicanMaintenanceRecord/myList")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", testMechanicId)
                .requestAttr("role", UserRoleEnum.TECHNICIAN.getCode())
                .content(objectMapper.writeValueAsString(queryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[*].status", everyItem(is(1))));
    }

    @Test
    @Transactional
    void testListMyTasksWithDeviceFilter() throws Exception {
        // 创建维修记录
        MechanicanMaintenanceRecordPo record = new MechanicanMaintenanceRecordPo();
        record.setUserId(testMechanicId);
        record.setDeviceId(testDeviceId);
        record.setDescription("特定设备的维修");
        record.setStatus(1);
        record.setUserMaintenanceRecordId(testUserMaintenanceRecordId);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        mechanicanMaintenanceRecordMapper.insert(record);

        // 准备查询参数，按设备ID过滤
        MechanicanQueryDto queryDto = new MechanicanQueryDto();
        queryDto.setPageNumber(1);
        queryDto.setPageSize(10);
        queryDto.setDeviceId(testDeviceId);

        // 发送查询请求
        mockMvc.perform(post("/mechanicanMaintenanceRecord/myList")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", testMechanicId)
                .requestAttr("role", UserRoleEnum.TECHNICIAN.getCode())
                .content(objectMapper.writeValueAsString(queryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.records[*].deviceId", everyItem(is(testDeviceId.intValue()))));
    }

    @Test
    @Transactional
    void testGetRecordDetail_NotFound() throws Exception {
        // 查询不存在的记录
        MechanicRecordQueryDto queryDto = new MechanicRecordQueryDto();
        queryDto.setDeviceId(99999L);
        queryDto.setStatus(1);

        mockMvc.perform(post("/mechanicanMaintenanceRecord/getRecordDetail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(greaterThan(0)));
    }
}

