//package group5.sebm.Maintenance.Integration;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import group5.sebm.BorrowRecord.dao.BorrowRecordMapper;
//import group5.sebm.BorrowRecord.entity.BorrowRecordPo;
//import group5.sebm.Device.dao.DeviceMapper;
//import group5.sebm.Device.entity.DevicePo;
//import group5.sebm.Maintenance.controller.dto.UserCreateDto;
//import group5.sebm.Maintenance.controller.dto.UserQueryDto;
//import group5.sebm.Maintenance.dao.UserMaintenanceRecordMapper;
//import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
//import group5.sebm.User.dao.UserMapper;
//import group5.sebm.User.entity.UserPo;
//import group5.sebm.common.dto.DeleteDto;
//import group5.sebm.common.enums.BorrowStatusEnum;
//import group5.sebm.common.enums.DeviceStatusEnum;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//
//import static org.hamcrest.Matchers.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * 用户维修记录集成测试
// * 测试完整的API流程，包括数据库交互
// */
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import(IntegrationTestConfig.class)
//@TestPropertySource(properties = {
//    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"
//})
//public class UserMaintenanceRecordIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private UserMaintenanceRecordMapper userMaintenanceRecordMapper;
//
//    @Autowired
//    private DeviceMapper deviceMapper;
//
//    @Autowired
//    private UserMapper userMapper;
//
//    @Autowired
//    private BorrowRecordMapper borrowRecordMapper;
//
//    private Long testUserId;
//    private Long testDeviceId;
//    private Long testBorrowRecordId;
//
//    @BeforeEach
//    void setUp() {
//        // 创建测试用户
//        UserPo testUser = new UserPo();
//        testUser.setUsername("testuser_maintenance");
//        testUser.setPassword("password123");
//        testUser.setEmail("test@example.com");
//        testUser.setUserRole(1);
//        testUser.setCreateTime(new Date());
//        testUser.setUpdateTime(new Date());
//        userMapper.insert(testUser);
//        testUserId = testUser.getId();
//
//        // 创建测试设备
//        DevicePo testDevice = new DevicePo();
//        testDevice.setDeviceName("Test Device");
//        testDevice.setDeviceType("Laptop");
//        testDevice.setStatus(DeviceStatusEnum.BORROWED.getCode());
//        testDevice.setLocation("Lab 1");
//        testDevice.setCreateTime(new Date());
//        testDevice.setUpdateTime(new Date());
//        deviceMapper.insert(testDevice);
//        testDeviceId = testDevice.getId();
//
//        // 创建测试借用记录
//        BorrowRecordPo testBorrowRecord = new BorrowRecordPo();
//        testBorrowRecord.setUserId(testUserId);
//        testBorrowRecord.setDeviceId(testDeviceId);
//        testBorrowRecord.setStatus(BorrowStatusEnum.BORROWED.getCode());
//        testBorrowRecord.setBorrowTime(new Date());
//        // 设置应还时间为7天后
//        Date dueTime = new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L);
//        testBorrowRecord.setDueTime(dueTime);
//        testBorrowRecord.setCreateTime(new Date());
//        testBorrowRecord.setUpdateTime(new Date());
//        borrowRecordMapper.insert(testBorrowRecord);
//        testBorrowRecordId = testBorrowRecord.getId();
//    }
//
//    @AfterEach
//    void tearDown() {
//        // 清理测试数据
//        if (testBorrowRecordId != null) {
//            borrowRecordMapper.deleteById(testBorrowRecordId);
//        }
//        if (testDeviceId != null) {
//            deviceMapper.deleteById(testDeviceId);
//        }
//        if (testUserId != null) {
//            userMapper.deleteById(testUserId);
//        }
//        // 清理维修记录
//        QueryWrapper<UserMaintenanceRecordPo> wrapper = new QueryWrapper<>();
//        wrapper.eq("userId", testUserId);
//        userMaintenanceRecordMapper.delete(wrapper);
//    }
//
//    @Test
//    @Transactional
//    void testCreateMaintenanceRecord_Success() throws Exception {
//        // 准备请求数据
//        UserCreateDto createDto = new UserCreateDto();
//        createDto.setBorrowRecordId(testBorrowRecordId);
//        createDto.setDescription("设备屏幕损坏");
//        createDto.setImage("http://example.com/image.jpg");
//
//        // 发送POST请求
//        mockMvc.perform(post("/userMaintenanceRecord/report")
//                .contentType(MediaType.APPLICATION_JSON)
//                .requestAttr("userId", testUserId)
//                .content(objectMapper.writeValueAsString(createDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.data.deviceId").value(testDeviceId))
//                .andExpect(jsonPath("$.data.userId").value(testUserId))
//                .andExpect(jsonPath("$.data.description").value("设备屏幕损坏"))
//                .andReturn();
//
//        // 验证数据库中的数据
//        QueryWrapper<UserMaintenanceRecordPo> wrapper = new QueryWrapper<>();
//        wrapper.eq("userId", testUserId).eq("deviceId", testDeviceId);
//        UserMaintenanceRecordPo record = userMaintenanceRecordMapper.selectOne(wrapper);
//        assertNotNull(record);
//        assertEquals("设备屏幕损坏", record.getDescription());
//
//        // 验证设备状态是否更新为维修中
//        DevicePo device = deviceMapper.selectById(testDeviceId);
//        assertEquals(DeviceStatusEnum.MAINTENANCE.getCode(), device.getStatus());
//    }
//
//    @Test
//    @Transactional
//    void testCreateMaintenanceRecord_Unauthorized() throws Exception {
//        // 使用不同的用户ID
//        Long unauthorizedUserId = testUserId + 1000;
//
//        UserCreateDto createDto = new UserCreateDto();
//        createDto.setBorrowRecordId(testBorrowRecordId);
//        createDto.setDescription("设备故障");
//
//        // 发送请求，预期失败
//        mockMvc.perform(post("/userMaintenanceRecord/report")
//                .contentType(MediaType.APPLICATION_JSON)
//                .requestAttr("userId", unauthorizedUserId)
//                .content(objectMapper.writeValueAsString(createDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(greaterThan(0)));
//    }
//
//    @Test
//    @Transactional
//    void testListMyRecords() throws Exception {
//        // 先创建一条维修记录
//        UserMaintenanceRecordPo record = new UserMaintenanceRecordPo();
//        record.setUserId(testUserId);
//        record.setDeviceId(testDeviceId);
//        record.setDescription("测试维修记录");
//        record.setStatus(0);
//        record.setCreateTime(new Date());
//        record.setUpdateTime(new Date());
//        userMaintenanceRecordMapper.insert(record);
//
//        // 准备查询参数
//        UserQueryDto queryDto = new UserQueryDto();
//        queryDto.setPageNumber(1);
//        queryDto.setPageSize(10);
//
//        // 发送查询请求
//        mockMvc.perform(post("/userMaintenanceRecord/myList")
//                .contentType(MediaType.APPLICATION_JSON)
//                .requestAttr("userId", testUserId)
//                .content(objectMapper.writeValueAsString(queryDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
//                .andExpect(jsonPath("$.data[0].userId").value(testUserId))
//                .andExpect(jsonPath("$.data[0].description").value("测试维修记录"));
//    }
//
//    @Test
//    @Transactional
//    void testGetRecordDetail_Success() throws Exception {
//        // 先创建一条维修记录
//        UserMaintenanceRecordPo record = new UserMaintenanceRecordPo();
//        record.setUserId(testUserId);
//        record.setDeviceId(testDeviceId);
//        record.setDescription("详细测试记录");
//        record.setStatus(0);
//        record.setCreateTime(new Date());
//        record.setUpdateTime(new Date());
//        userMaintenanceRecordMapper.insert(record);
//        Long recordId = record.getId();
//
//        // 获取记录详情
//        mockMvc.perform(get("/userMaintenanceRecord/" + recordId)
//                .requestAttr("userId", testUserId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.data.id").value(recordId))
//                .andExpect(jsonPath("$.data.description").value("详细测试记录"));
//    }
//
//    @Test
//    @Transactional
//    void testCancelRecord_Success() throws Exception {
//        // 先创建一条维修记录
//        UserMaintenanceRecordPo record = new UserMaintenanceRecordPo();
//        record.setUserId(testUserId);
//        record.setDeviceId(testDeviceId);
//        record.setDescription("待取消的记录");
//        record.setStatus(0);  // 待处理状态
//        record.setCreateTime(new Date());
//        record.setUpdateTime(new Date());
//        userMaintenanceRecordMapper.insert(record);
//        Long recordId = record.getId();
//
//        // 准备取消请求
//        DeleteDto deleteDto = new DeleteDto();
//        deleteDto.setId(recordId);
//
//        // 发送取消请求
//        mockMvc.perform(post("/userMaintenanceRecord/cancel")
//                .contentType(MediaType.APPLICATION_JSON)
//                .requestAttr("userId", testUserId)
//                .content(objectMapper.writeValueAsString(deleteDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.data").value(true));
//
//        // 验证记录已被逻辑删除
//        // 注意：selectById在有@TableLogic的情况下，会自动添加isDelete=0的条件
//        // 所以逻辑删除后应该查不到
//        UserMaintenanceRecordPo checkRecord = userMaintenanceRecordMapper.selectById(recordId);
//        assertNull(checkRecord, "逻辑删除后，selectById应该查不到记录");
//    }
//
//    @Test
//    @Transactional
//    void testGetAllRecords() throws Exception {
//        // 创建多条维修记录
//        for (int i = 0; i < 3; i++) {
//            UserMaintenanceRecordPo record = new UserMaintenanceRecordPo();
//            record.setUserId(testUserId);
//            record.setDeviceId(testDeviceId);
//            record.setDescription("批量测试记录 " + i);
//            record.setStatus(i % 2);
//            record.setCreateTime(new Date());
//            record.setUpdateTime(new Date());
//            userMaintenanceRecordMapper.insert(record);
//        }
//
//        // 准备查询参数
//        UserQueryDto queryDto = new UserQueryDto();
//        queryDto.setPageNumber(1);
//        queryDto.setPageSize(10);
//
//        // 查询所有记录
//        mockMvc.perform(post("/userMaintenanceRecord/getAllList")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(queryDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
//    }
//
//    @Test
//    @Transactional
//    void testListMyRecordsWithStatusFilter() throws Exception {
//        // 创建不同状态的维修记录
//        UserMaintenanceRecordPo record1 = new UserMaintenanceRecordPo();
//        record1.setUserId(testUserId);
//        record1.setDeviceId(testDeviceId);
//        record1.setDescription("待处理记录");
//        record1.setStatus(0);
//        record1.setCreateTime(new Date());
//        record1.setUpdateTime(new Date());
//        userMaintenanceRecordMapper.insert(record1);
//
//        UserMaintenanceRecordPo record2 = new UserMaintenanceRecordPo();
//        record2.setUserId(testUserId);
//        record2.setDeviceId(testDeviceId);
//        record2.setDescription("已处理记录");
//        record2.setStatus(1);
//        record2.setCreateTime(new Date());
//        record2.setUpdateTime(new Date());
//        userMaintenanceRecordMapper.insert(record2);
//
//        // 查询状态为0的记录
//        UserQueryDto queryDto = new UserQueryDto();
//        queryDto.setPageNumber(1);
//        queryDto.setPageSize(10);
//        queryDto.setStatus(0);
//
//        mockMvc.perform(post("/userMaintenanceRecord/myList")
//                .contentType(MediaType.APPLICATION_JSON)
//                .requestAttr("userId", testUserId)
//                .content(objectMapper.writeValueAsString(queryDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.data[*].status", everyItem(is(0))));
//    }
//
//    @Test
//    @Transactional
//    void testCreateMaintenanceRecord_MissingLoginInfo() throws Exception {
//        UserCreateDto createDto = new UserCreateDto();
//        createDto.setBorrowRecordId(testBorrowRecordId);
//        createDto.setDescription("测试未登录");
//
//        // 不提供userId，预期失败
//        mockMvc.perform(post("/userMaintenanceRecord/report")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(createDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(greaterThan(0)));
//    }
//}
//
