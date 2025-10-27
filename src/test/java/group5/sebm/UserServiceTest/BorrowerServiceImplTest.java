package group5.sebm.UserServiceTest;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import group5.sebm.User.controller.dto.*;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.dao.UserMapper;
import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.BorrowerServiceImpl;
import group5.sebm.User.service.UserServiceImpl;
import group5.sebm.User.service.UserServiceInterface.UserService;
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
    private UserMapper userMapper;
    @InjectMocks
    private BorrowerServiceImpl borrowerService;// 依赖 mock

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

        when(userMapper.selectById(1L)).thenReturn(userPo);
        when(userMapper.updateById(any(UserPo.class))).thenThrow(new RuntimeException("DB error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> this.deactivateUser(dto));

        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("Deactivate failed"));
    }
    @Test
    void testUpdateBorrowedCount_withNullParams_shouldThrowException() {
        BusinessException ex1 = assertThrows(BusinessException.class,
                () -> borrowerService.updateBorrowedCount(null, 5));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex1.getCode());

        BusinessException ex2 = assertThrows(BusinessException.class,
                () -> borrowerService.updateBorrowedCount(1L, null));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), ex2.getCode());
    }

    /**
     * 测试：用户不存在时抛出 BusinessException
     */
    @Test
    void testUpdateBorrowedCount_userNotFound_shouldThrowException() {
        BorrowerServiceImpl spyService = spy(borrowerService);
        doReturn(null).when(spyService).getById(999L); // 模拟找不到用户

        BusinessException ex = assertThrows(BusinessException.class,
                () -> spyService.updateBorrowedCount(999L, 3));
        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
    }

    /**
     * 测试：正常更新 borrowedCount
     */
    @Test
    void testUpdateBorrowedCount_success() {
        BorrowerServiceImpl spyService = spy(borrowerService);
        UserPo mockUser = new UserPo();
        mockUser.setId(1L);
        mockUser.setBorrowedDeviceCount(5);

        doReturn(mockUser).when(spyService).getById(1L);
        doReturn(true).when(spyService).updateById(any(UserPo.class));

        Boolean result = spyService.updateBorrowedCount(1L, 1);

        assertTrue(result);
        verify(spyService, times(1)).getById(1L);
        verify(spyService, times(1)).updateById(any(UserPo.class));
        assertEquals(6, mockUser.getBorrowedDeviceCount());
    }

    /**
     * 测试：更新过程出现异常时抛出 SYSTEM_ERROR
     */
    @Test
    void testUpdateBorrowedCount_updateFails_shouldThrowSystemError() {
        BorrowerServiceImpl spyService = spy(borrowerService);
        UserPo mockUser = new UserPo();
        mockUser.setId(1L);
        mockUser.setBorrowedDeviceCount(2);

        doReturn(mockUser).when(spyService).getById(1L);
        doThrow(new RuntimeException("DB update failed")).when(spyService).updateById(any(UserPo.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> spyService.updateBorrowedCount(1L, 10));
        assertEquals(ErrorCode.SYSTEM_ERROR.getCode(), ex.getCode());
        assertTrue(ex.getMessage().contains("Update borrowedCount failed"));
    }
    @Test
    void testGetCurrentUserDtoFromID_Success() {
        // mock 查询结果
        UserPo mockUser = new UserPo();
        mockUser.setId(1L);
        mockUser.setUsername("jack");
        when(userMapper.selectById(1L)).thenReturn(mockUser);

        // 调用方法
        group5.sebm.common.dto.UserDto dto = this.getCurrentUserDtoFromID(1L);

        assertNotNull(dto);
        assertEquals("jack", dto.getUsername());
    }


    @Test
    void testGetCurrentUserDtoFromHttp_NotLogin() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> this.getCurrentUserDtoFromHttp(request));
        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), ex.getCode());
    }

    @Test
    void testGetCurrentUserDtoFromHttp_UserNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(2L);
        when(userMapper.selectById(2L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> this.getCurrentUserDtoFromHttp(request));

        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
        assertEquals("User not found", ex.getMessage());
    }

    // --------------------------
    // 测试 getCurrentUserDtoFromID()
    // --------------------------

    @Test
    void testGetCurrentUserDtoFromID_UserIdNull() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> this.getCurrentUserDtoFromID(null));
        assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), ex.getCode());
    }

    @Test
    void testGetCurrentUserDtoFromID_UserNotFound() {
        when(baseMapper.selectById(999L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> this.getCurrentUserDtoFromID(999L));
        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), ex.getCode());
    }
}
