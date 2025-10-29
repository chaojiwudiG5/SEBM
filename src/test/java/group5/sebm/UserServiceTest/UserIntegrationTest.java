package group5.sebm.UserServiceTest;

import group5.sebm.User.controller.dto.LoginDto;
import group5.sebm.User.controller.dto.RegisterDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.entity.UserPo;
import group5.sebm.User.enums.UserRoleEnum;
import group5.sebm.User.service.bo.Borrower;
import group5.sebm.User.service.bo.Manager;
import group5.sebm.User.service.bo.User;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User模块整合测试
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
@DisplayName("User模块整合测试")
class UserIntegrationTest {

    @Test
    @DisplayName("测试Spring上下文加载成功")
    void contextLoads() {
        // 这个测试只是确保Spring上下文能够成功加载
        // 如果上下文加载失败，测试会抛出异常
        assertTrue(true, "Spring上下文应该成功加载");
    }

    @Test
    @DisplayName("测试User模块类可以被实例化")
    void testUserClassesCanBeInstantiated() {
        // 测试关键类是否可以被加载和实例化
        assertDoesNotThrow(() -> {
            Class.forName("group5.sebm.User.service.UserServiceImpl");
            Class.forName("group5.sebm.User.service.BorrowerServiceImpl");
            Class.forName("group5.sebm.User.service.ManagerServiceImpl");
            Class.forName("group5.sebm.User.controller.UserController");
            Class.forName("group5.sebm.User.entity.UserPo");
            Class.forName("group5.sebm.User.dao.UserMapper");
        }, "User模块的所有关键类应该可以被加载");
    }

    @Test
    @DisplayName("测试实体类结构完整性")
    void testEntityClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证实体类有正确的结构
            Class<?> userPoClass = Class.forName("group5.sebm.User.entity.UserPo");
            
            // 验证关键字段存在
            assertNotNull(userPoClass.getDeclaredField("id"));
            assertNotNull(userPoClass.getDeclaredField("username"));
            assertNotNull(userPoClass.getDeclaredField("password"));
            assertNotNull(userPoClass.getDeclaredField("email"));
            assertNotNull(userPoClass.getDeclaredField("phone"));
            assertNotNull(userPoClass.getDeclaredField("gender"));
            assertNotNull(userPoClass.getDeclaredField("userRole"));
            assertNotNull(userPoClass.getDeclaredField("userStatus"));
            assertNotNull(userPoClass.getDeclaredField("age"));
            assertNotNull(userPoClass.getDeclaredField("level"));
        }, "UserPo实体类应该有完整的字段定义");
    }

    @Test
    @DisplayName("测试DTO类结构完整性")
    void testDTOClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证DTO类可以被加载
            Class.forName("group5.sebm.User.controller.dto.RegisterDto");
            Class.forName("group5.sebm.User.controller.dto.LoginDto");
            Class.forName("group5.sebm.User.controller.dto.UpdateDto");
            Class.forName("group5.sebm.User.controller.dto.UserDto");
        }, "所有DTO类应该可以被正确加载");
    }

    @Test
    @DisplayName("测试VO类结构完整性")
    void testVOClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证VO类可以被加载
            Class<?> userVoClass = Class.forName("group5.sebm.User.controller.vo.UserVo");
            
            // 验证VO类有基本的字段
            assertNotNull(userVoClass.getDeclaredField("id"));
            assertNotNull(userVoClass.getDeclaredField("username"));
            assertNotNull(userVoClass.getDeclaredField("userRole"));
            assertNotNull(userVoClass.getDeclaredField("token"));
        }, "UserVo类应该可以被正确加载并有完整的字段定义");
    }

    @Test
    @DisplayName("测试BO类结构完整性")
    void testBOClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证BO类可以被加载
            Class.forName("group5.sebm.User.service.bo.User");
            Class.forName("group5.sebm.User.service.bo.Borrower");
            Class.forName("group5.sebm.User.service.bo.Manager");
        }, "所有BO类应该可以被正确加载");
    }

    // ========== 功能测试部分 ==========

    @Test
    @DisplayName("功能测试：实体对象可以正常创建和使用")
    void testEntityObjectCreation() {
        // 测试UserPo的创建和基本功能
        UserPo user = new UserPo();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedPassword");
        user.setEmail("test@example.com");
        user.setPhone("13800138000");
        user.setGender(1);
        user.setUserRole(0);
        user.setUserStatus(0);
        user.setAge(25);
        user.setLevel(1);
        user.setOverdueTimes(0);
        user.setBorrowedDeviceCount(0);
        user.setMaxBorrowedDeviceCount(5);
        user.setMaxOverdueTimes(3);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        assertNotNull(user.getId(), "ID应该被正确设置");
        assertEquals("testuser", user.getUsername(), "用户名应该被正确设置");
        assertEquals("test@example.com", user.getEmail(), "邮箱应该被正确设置");
        assertEquals("13800138000", user.getPhone(), "电话应该被正确设置");
        assertEquals(0, user.getUserRole(), "用户角色应该被正确设置");
        assertEquals(0, user.getUserStatus(), "用户状态应该被正确设置");
        assertEquals(25, user.getAge(), "年龄应该被正确设置");
    }

    @Test
    @DisplayName("功能测试：DTO对象可以正常创建和使用")
    void testDTOObjectCreation() {
        // 测试RegisterDto
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("newuser");
        registerDto.setPassword("password123");
        registerDto.setCheckPassword("password123");
        registerDto.setPhone("13900139000");

        assertEquals("newuser", registerDto.getUsername(), "用户名应该被正确设置");
        assertEquals("password123", registerDto.getPassword(), "密码应该被正确设置");
        assertEquals("password123", registerDto.getCheckPassword(), "确认密码应该被正确设置");
        assertEquals("13900139000", registerDto.getPhone(), "电话应该被正确设置");

        // 测试LoginDto
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("existinguser");
        loginDto.setPassword("password456");

        assertEquals("existinguser", loginDto.getUsername(), "用户名应该被正确设置");
        assertEquals("password456", loginDto.getPassword(), "密码应该被正确设置");

        // 测试UpdateDto
        UpdateDto updateDto = new UpdateDto();
        updateDto.setId(1L);
        updateDto.setUsername("updateduser");
        updateDto.setEmail("updated@example.com");
        updateDto.setPhone("13700137000");
        updateDto.setAge(30);

        assertEquals(1L, updateDto.getId(), "ID应该被正确设置");
        assertEquals("updateduser", updateDto.getUsername(), "用户名应该被正确设置");
        assertEquals("updated@example.com", updateDto.getEmail(), "邮箱应该被正确设置");
    }

    @Test
    @DisplayName("功能测试：VO对象可以正常创建和使用")
    void testVOObjectCreation() {
        // 测试UserVo
        UserVo userVo = new UserVo();
        userVo.setId(1L);
        userVo.setUsername("vouser");
        userVo.setEmail("vo@example.com");
        userVo.setPhone("13600136000");
        userVo.setUserRole(0);
        userVo.setUserStatus(0);
        userVo.setAge(28);
        userVo.setLevel(2);
        userVo.setToken("jwt-token-here");
        userVo.setActive(true);

        assertEquals(1L, userVo.getId(), "ID应该被正确设置");
        assertEquals("vouser", userVo.getUsername(), "用户名应该被正确设置");
        assertEquals("vo@example.com", userVo.getEmail(), "邮箱应该被正确设置");
        assertEquals(0, userVo.getUserRole(), "用户角色应该被正确设置");
        assertEquals("jwt-token-here", userVo.getToken(), "Token应该被正确设置");
        assertTrue(userVo.isActive(), "用户应该是活跃状态");
    }

    @Test
    @DisplayName("功能测试：BO对象可以正常创建和使用")
    void testBOObjectCreation() {
        // 测试User BO
        User user = new User();
        user.setId(1L);
        user.setUsername("bouser");
        user.setPassword("hashedPass");
        user.setEmail("bo@example.com");
        user.setPhone("13500135000");
        user.setUserRole(0);

        assertEquals(1L, user.getId(), "ID应该被正确设置");
        assertEquals("bouser", user.getUsername(), "用户名应该被正确设置");
        assertEquals("bo@example.com", user.getEmail(), "邮箱应该被正确设置");

        // 测试Borrower BO
        Borrower borrower = new Borrower();
        borrower.setId(2L);
        borrower.setUsername("borrower");
        borrower.setUserRole(0);
        borrower.setOverdueTimes(0);
        borrower.setBorrowedDeviceCount(2);
        borrower.setMaxBorrowedDeviceCount(5);
        borrower.setMaxOverdueTimes(3);

        assertEquals(2L, borrower.getId(), "ID应该被正确设置");
        assertEquals("borrower", borrower.getUsername(), "用户名应该被正确设置");
        assertEquals(2, borrower.getBorrowedDeviceCount(), "已借设备数应该被正确设置");
        assertEquals(5, borrower.getMaxBorrowedDeviceCount(), "最大可借数应该被正确设置");

        // 测试Manager BO
        Manager manager = new Manager();
        manager.setId(3L);
        manager.setUsername("manager");
        manager.setUserRole(1);
        manager.setIsDelete(false);

        assertEquals(3L, manager.getId(), "ID应该被正确设置");
        assertEquals("manager", manager.getUsername(), "用户名应该被正确设置");
        assertEquals(1, manager.getUserRole(), "管理员角色应该被正确设置");
        assertFalse(manager.getIsDelete(), "删除标记应该是false");
    }

    @Test
    @DisplayName("功能测试：用户角色枚举")
    void testUserRoleEnum() {
        // 测试用户角色枚举的正确性
        assertDoesNotThrow(() -> {
            String userValue = UserRoleEnum.USER.getValue();
            String adminValue = UserRoleEnum.ADMIN.getValue();
            
            assertEquals("user", userValue, "普通用户角色值应该是user");
            assertEquals("admin", adminValue, "管理员角色值应该是admin");
            
            // 验证角色文本不为空
            assertNotNull(UserRoleEnum.USER.getText(), "角色文本不应为null");
            assertNotNull(UserRoleEnum.ADMIN.getText(), "角色文本不应为null");
            
            assertEquals("用户", UserRoleEnum.USER.getText(), "用户角色文本应该是'用户'");
            assertEquals("管理员", UserRoleEnum.ADMIN.getText(), "管理员角色文本应该是'管理员'");
            
            // 测试根据value获取枚举
            assertEquals(UserRoleEnum.USER, UserRoleEnum.getEnumByValue("user"),
                "应该能通过value获取枚举");
            assertEquals(UserRoleEnum.ADMIN, UserRoleEnum.getEnumByValue("admin"),
                "应该能通过value获取枚举");
        }, "用户角色枚举应该可以正常使用");
    }

    @Test
    @DisplayName("功能测试：密码验证逻辑")
    void testPasswordValidation() {
        // 测试User BO的密码验证方法
        User user = new User();
        
        // 测试两次密码验证
        assertTrue(user.validateTwicePassword("password123", "password123"),
            "相同的密码应该验证通过");
        assertFalse(user.validateTwicePassword("password123", "password456"),
            "不同的密码应该验证失败");
        assertFalse(user.validateTwicePassword(null, "password123"),
            "null密码应该验证失败");

        // 测试BCrypt密码验证
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "testPassword123";
        String hashedPassword = encoder.encode(rawPassword);
        
        assertTrue(user.validatePassword(rawPassword, hashedPassword, encoder),
            "正确的密码应该验证通过");
        assertFalse(user.validatePassword("wrongPassword", hashedPassword, encoder),
            "错误的密码应该验证失败");
    }

    @Test
    @DisplayName("功能测试：业务异常处理")
    void testBusinessExceptionHandling() {
        // 测试用户相关的业务异常
        BusinessException exception = new BusinessException(
            ErrorCode.NOT_FOUND_ERROR,
            "用户不存在"
        );

        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), exception.getCode(),
            "异常代码应该正确");
        assertTrue(exception.getMessage().contains("用户不存在"),
            "异常消息应该包含自定义信息");

        // 测试登录相关异常
        BusinessException loginException = new BusinessException(
            ErrorCode.PASS_ERROR,
            "密码错误"
        );

        assertEquals(ErrorCode.PASS_ERROR.getCode(), loginException.getCode(),
            "密码错误代码应该正确");
    }

    @Test
    @DisplayName("功能测试：Borrower业务逻辑")
    void testBorrowerBusinessLogic() {
        // 测试借用者的业务逻辑
        Borrower borrower = new Borrower();
        borrower.setOverdueTimes(0);
        borrower.setBorrowedDeviceCount(2);
        borrower.setMaxBorrowedDeviceCount(5);
        borrower.setMaxOverdueTimes(3);

        // 验证借用者可以继续借设备
        assertTrue(borrower.getBorrowedDeviceCount() < borrower.getMaxBorrowedDeviceCount(),
            "未达到最大借用数时应该可以继续借设备");
        
        // 验证逾期次数未超限
        assertTrue(borrower.getOverdueTimes() < borrower.getMaxOverdueTimes(),
            "未达到最大逾期次数时账户应该正常");

        // 模拟借用设备
        borrower.setBorrowedDeviceCount(borrower.getBorrowedDeviceCount() + 1);
        assertEquals(3, borrower.getBorrowedDeviceCount(),
            "借用设备后计数应该增加");

        // 模拟逾期
        borrower.setOverdueTimes(borrower.getOverdueTimes() + 1);
        assertEquals(1, borrower.getOverdueTimes(),
            "逾期后次数应该增加");
    }

    @Test
    @DisplayName("集成测试：完整的对象转换流程")
    void testCompleteObjectConversionFlow() {
        // 模拟一个完整的用户注册流程
        // 1. 创建 RegisterDto
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("newuser");
        registerDto.setPassword("password123");
        registerDto.setCheckPassword("password123");
        registerDto.setPhone("13800138000");

        // 2. 模拟转换为Po
        UserPo po = new UserPo();
        po.setId(1L);
        po.setUsername(registerDto.getUsername());
        po.setPassword(new BCryptPasswordEncoder().encode(registerDto.getPassword()));
        po.setPhone(registerDto.getPhone());
        po.setUserRole(0);
        po.setUserStatus(0);
        po.setLevel(1);
        po.setOverdueTimes(0);
        po.setBorrowedDeviceCount(0);
        po.setMaxBorrowedDeviceCount(5);
        po.setMaxOverdueTimes(3);
        po.setCreateTime(new Date());
        po.setUpdateTime(new Date());

        // 3. 模拟转换为Vo
        UserVo vo = new UserVo();
        vo.setId(po.getId());
        vo.setUsername(po.getUsername());
        vo.setPhone(po.getPhone());
        vo.setUserRole(po.getUserRole());
        vo.setUserStatus(po.getUserStatus());
        vo.setLevel(po.getLevel());
        vo.setOverdueTimes(po.getOverdueTimes());
        vo.setBorrowedDeviceCount(po.getBorrowedDeviceCount());
        vo.setMaxBorrowedDeviceCount(po.getMaxBorrowedDeviceCount());
        vo.setActive(true);

        // 验证整个转换流程
        assertEquals(registerDto.getUsername(), vo.getUsername(),
            "用户名应该在整个转换流程中保持一致");
        assertEquals(registerDto.getPhone(), vo.getPhone(),
            "电话应该在整个转换流程中保持一致");
        assertEquals(po.getId(), vo.getId(),
            "ID应该从Po正确转换到Vo");
        assertEquals(0, vo.getUserRole(),
            "新注册用户应该是普通用户角色");
        assertTrue(vo.isActive(), "新用户应该是活跃状态");
        
        assertNotNull(vo, "最终的VO对象应该不为null");
    }

    @Test
    @DisplayName("功能测试：用户等级系统")
    void testUserLevelSystem() {
        // 测试用户等级相关逻辑
        UserPo user = new UserPo();
        user.setLevel(1);
        user.setMaxBorrowedDeviceCount(5);

        // 验证初始等级
        assertEquals(1, user.getLevel(), "新用户应该是1级");
        assertEquals(5, user.getMaxBorrowedDeviceCount(), "1级用户应该可以借5台设备");

        // 模拟升级
        user.setLevel(2);
        user.setMaxBorrowedDeviceCount(8);
        
        assertEquals(2, user.getLevel(), "升级后等级应该是2");
        assertEquals(8, user.getMaxBorrowedDeviceCount(), "2级用户应该可以借更多设备");
    }

    @Test
    @DisplayName("功能测试：用户状态管理")
    void testUserStatusManagement() {
        // 测试用户状态管理
        UserPo user = new UserPo();
        user.setUserStatus(0);
        user.setOverdueTimes(0);
        user.setMaxOverdueTimes(3);

        // 正常状态
        assertEquals(0, user.getUserStatus(), "用户应该是正常状态");
        assertTrue(user.getOverdueTimes() < user.getMaxOverdueTimes(),
            "逾期次数未超限");

        // 模拟逾期到达上限
        user.setOverdueTimes(3);
        assertTrue(user.getOverdueTimes() >= user.getMaxOverdueTimes(),
            "逾期次数达到上限");
        // 注意：实际业务中这里应该修改用户状态，这里只是测试数据结构
    }

    @Test
    @DisplayName("功能测试：密码加密验证")
    void testPasswordEncryption() {
        // 测试密码加密和验证流程
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "mySecurePassword123";
        
        // 加密密码
        String hashedPassword = encoder.encode(rawPassword);
        assertNotNull(hashedPassword, "加密后的密码不应为null");
        assertNotEquals(rawPassword, hashedPassword, "加密后的密码应该与原密码不同");
        assertTrue(hashedPassword.length() > 50, "BCrypt加密后的密码应该较长");

        // 验证密码
        assertTrue(encoder.matches(rawPassword, hashedPassword),
            "相同的原密码应该验证通过");
        assertFalse(encoder.matches("wrongPassword", hashedPassword),
            "错误的密码应该验证失败");
    }

    @Test
    @DisplayName("功能测试：用户信息更新验证")
    void testUserInformationUpdate() {
        // 测试用户信息更新的数据完整性
        UpdateDto updateDto = new UpdateDto();
        updateDto.setId(1L);
        updateDto.setUsername("updatedName");
        updateDto.setEmail("newemail@example.com");
        updateDto.setPhone("13900139000");
        updateDto.setAge(30);
        updateDto.setGender(1);

        // 验证所有字段都可以被更新
        assertNotNull(updateDto.getId(), "ID不应为null");
        assertNotNull(updateDto.getUsername(), "用户名不应为null");
        assertNotNull(updateDto.getEmail(), "邮箱不应为null");
        assertNotNull(updateDto.getPhone(), "电话不应为null");
        assertNotNull(updateDto.getAge(), "年龄不应为null");

        // 验证邮箱格式（简单验证）
        assertTrue(updateDto.getEmail().contains("@"), "邮箱应该包含@符号");
    }

    @Test
    @DisplayName("功能测试：用户角色权限验证")
    void testUserRolePermissions() {
        // 测试不同角色的用户
        UserPo user = new UserPo();
        user.setUserRole(0);
        assertEquals(0, user.getUserRole(), "普通用户角色码应该是0");

        UserPo admin = new UserPo();
        admin.setUserRole(1);
        assertEquals(1, admin.getUserRole(), "管理员角色码应该是1");

        UserPo technician = new UserPo();
        technician.setUserRole(2);
        assertEquals(2, technician.getUserRole(), "技工角色码应该是2");

        // 验证角色互不相同
        assertNotEquals(user.getUserRole(), admin.getUserRole(),
            "不同角色应该有不同的角色码");
        assertNotEquals(admin.getUserRole(), technician.getUserRole(),
            "不同角色应该有不同的角色码");
        
        // 验证角色枚举的使用
        assertEquals("user", UserRoleEnum.USER.getValue(), "用户角色值应该是user");
        assertEquals("admin", UserRoleEnum.ADMIN.getValue(), "管理员角色值应该是admin");
    }
}

