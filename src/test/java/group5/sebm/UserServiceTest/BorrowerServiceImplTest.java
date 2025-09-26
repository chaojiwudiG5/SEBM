package group5.sebm.UserServiceTest;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.LoginDto;
import group5.sebm.User.controller.dto.RegisterDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.dao.UserMapper;
import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.UserServiceImpl;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowerServiceImplTest extends UserServiceImpl {

    @Mock
    private UserMapper userMapper;         // 依赖 mock

    @Mock
    private HttpServletRequest request;    // mock Http 请求

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.baseMapper = userMapper;
    }

    // 注册用户：成功
    @Test
    void testUserRegister_Success() {
        RegisterDto dto = new RegisterDto();
        dto.setPhone("1234567890");
        dto.setPassword("password");
        dto.setCheckPassword("password");

        // 模拟数据库没有同 phone 的用户
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // 模拟插入用户
        doAnswer(invocation -> {
            UserPo userPo = invocation.getArgument(0);
            userPo.setId(1L);
            return 1;
        }).when(userMapper).insert(any(UserPo.class));

        Long userId = this.userRegister(dto);

        assertNotNull(userId);
        assertEquals(1L, userId);
    }

    // 注册用户：手机号已存在
    @Test
    void testUserRegister_UserAlreadyExists() {
        RegisterDto dto = new RegisterDto();
        dto.setPhone("1234567890");
        dto.setPassword("password");
        dto.setCheckPassword("password");

        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(new UserPo());

        Exception exception = assertThrows(RuntimeException.class,
                () -> this.userRegister(dto));

        assertTrue(exception.getMessage().contains("User already exists"));
    }

    // 注册用户：两次密码不一致
    @Test
    void testUserRegister_PasswordMismatch() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("username");
        dto.setPhone("1234567890");
        dto.setPassword("password");
        dto.setCheckPassword("wrongpass");

        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> this.userRegister(dto));

        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex.getCode());
        assertEquals("Passwords do not match", ex.getMessage());

    }

    // 登录：成功
    @Test
    void testUserLogin_Success() {
        LoginDto dto = new LoginDto();
        dto.setUsername("testUser");
        dto.setPassword("password");

        UserPo userPo = new UserPo();
        userPo.setId(1L);
        userPo.setUsername("testUser");
        userPo.setPassword(encoder.encode("password"));

        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(userPo);

        UserVo result = this.userLogin(dto);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertNotNull(result.getToken());
    }

    // 登录：用户不存在
    @Test
    void testUserLogin_UserNotExists() {
        LoginDto dto = new LoginDto();
        dto.setUsername("notExists");
        dto.setPassword("password");

        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class,
                () -> this.userLogin(dto));

        assertTrue(exception.getMessage().contains("Username not exists"));
    }

    // 登录：密码错误
    @Test
    void testUserLogin_WrongPassword() {
        LoginDto dto = new LoginDto();
        dto.setUsername("testUser");
        dto.setPassword("wrongpass");

        UserPo userPo = new UserPo();
        userPo.setId(1L);
        userPo.setUsername("testUser");
        userPo.setPassword(encoder.encode("password"));

        when(userMapper.selectOne(any(QueryWrapper.class)))
                .thenReturn(userPo);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> this.userLogin(dto));

        assertEquals(ErrorCode.PASS_ERROR.getCode(), ex.getCode());
        assertEquals("Password is incorrect", ex.getMessage());

    }

    // 获取当前用户：成功
    @Test
    void testGetCurrentUser_Success() {
        when(request.getAttribute("userId")).thenReturn(1L);

        UserPo userPo = new UserPo();
        userPo.setId(1L);
        userPo.setUsername("currentUser");

        when(userMapper.selectById(1L)).thenReturn(userPo);

        UserVo result = this.getCurrentUser(request);

        assertNotNull(result);
        assertEquals("currentUser", result.getUsername());
    }

    // 获取当前用户：未登录
    @Test
    void testGetCurrentUser_NotLogin() {
        when(request.getAttribute("userId")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class,
                () -> this.getCurrentUser(request));

        assertTrue(exception.getMessage().contains("Not login"));
    }

    // 更新用户：成功
    @Test
    void testUpdateUser_Success() {
        when(request.getAttribute("userId")).thenReturn(1L);

        UpdateDto dto = new UpdateDto();
        dto.setId(1L);
        dto.setUsername("updatedUser");

        UserPo oldUserPo = new UserPo();
        oldUserPo.setId(1L);
        oldUserPo.setUsername("oldUser");

        when(userMapper.selectById(1L)).thenReturn(oldUserPo);

        doAnswer(invocation -> 1).when(userMapper).updateById(any(UserPo.class));

        UserVo result = this.updateUser(dto, request);

        assertNotNull(result);
        assertEquals("updatedUser", result.getUsername());
    }

    // 更新用户：不是当前登录用户
    @Test
    void testUpdateUser_NotCurrentUser() {
        when(request.getAttribute("userId")).thenReturn(2L);

        UpdateDto dto = new UpdateDto();
        dto.setId(1L);

        Exception exception = assertThrows(RuntimeException.class,
                () -> this.updateUser(dto, request));

        assertTrue(exception.getMessage().contains("Not login"));
    }

    // 更新用户：用户不存在
    @Test
    void testUpdateUser_UserNotExists() {
        when(request.getAttribute("userId")).thenReturn(1L);

        UpdateDto dto = new UpdateDto();
        dto.setId(1L);

        when(userMapper.selectById(1L)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class,
                () -> this.updateUser(dto, request));

        assertTrue(exception.getMessage().contains("User not exists"));
    }
    @Test
    void testDeactivateUser_UserNotExists() {
        DeleteDto dto = new DeleteDto();
        dto.setId(99L);

        when(baseMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> this.deactivateUser(dto));

        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("User not exists"));
    }

    @Test
    void testDeactivateUser_UpdateFails() {
        DeleteDto dto = new DeleteDto();
        dto.setId(1L);

        UserPo userPo = new UserPo();
        userPo.setId(1L);
        userPo.setIsDelete(0);

        when(baseMapper.selectById(1L)).thenReturn(userPo);
        when(baseMapper.updateById(any(UserPo.class))).thenThrow(new RuntimeException("DB error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> this.deactivateUser(dto));

        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("Deactivate failed"));
    }
}
