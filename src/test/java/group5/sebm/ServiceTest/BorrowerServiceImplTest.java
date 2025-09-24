//package group5.sebm.ServiceTest;
//
//import static group5.sebm.common.constant.UserConstant.CURRENT_LOGIN_USER;
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import group5.sebm.controller.dto.DeleteDto;
//import group5.sebm.controller.dto.LoginDto;
//import group5.sebm.controller.dto.RegisterDto;
//import group5.sebm.controller.dto.UpdateDto;
//import group5.sebm.controller.vo.UserVo;
//import group5.sebm.dao.UserMapper;
//import group5.sebm.entity.UserPo;
//import group5.sebm.exception.ErrorCode;
//import group5.sebm.exception.ThrowUtils;
//import group5.sebm.service.BorrowerServiceImpl;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.lang.reflect.Field;
//
//@ExtendWith(MockitoExtension.class)
//class BorrowerServiceImplTest {
//
//    @InjectMocks
//    private BorrowerServiceImpl borrowerService;
//
//    @Mock
//    private UserMapper userMapper;
//
//    @Mock
//    private HttpServletRequest request;
//
//    @Mock
//    private HttpSession session;
//
//    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//    @BeforeEach
//    public void setUp() throws NoSuchFieldException, IllegalAccessException {
//        MockitoAnnotations.openMocks(this);
//
//        // 使用反射设置 baseMapper
//        Field baseMapperField = ServiceImpl.class.getDeclaredField("baseMapper");
//        baseMapperField.setAccessible(true);
//        baseMapperField.set(borrowerService, userMapper);
//    }
//
//    @Test
//    public void testGetLoginUser_WhenUserExists() {
//        // 准备一个 UserVo
//        UserVo mockUser = new UserVo();
//        mockUser.setId(1L);
//        mockUser.setUsername("testUser");
//
//        // mock session.getAttribute
//        when(session.getAttribute(CURRENT_LOGIN_USER)).thenReturn(mockUser);
//        HttpSession session = mock(HttpSession.class);
//        when(request.getSession()).thenReturn(session);
//
//        // 调用方法
//        UserVo userVo = borrowerService.getLoginUser(request);
//
//        // 验证返回值
//        assertNotNull(userVo);
//
//        assertEquals(1L, userVo.getId());
//        assertEquals("testUser", userVo.getUsername());
//    }
//
//    @Test
//    public void testGetLoginUser_WhenUserNotExists() {
//        when(request.getSession()).thenReturn(session);
//
//        when(session.getAttribute(CURRENT_LOGIN_USER)).thenReturn(null);
//
//        UserVo userVo = borrowerService.getLoginUser(request);
//
//        assertNull(userVo);
//    }
//
//    @Test
//    void testUserLogin_Success() {
//
//        LoginDto dto = new LoginDto();
//        dto.setUsername("tommy");
//        dto.setPassword("password");
//
//
//        UserPo userPo = new UserPo();
//        userPo.setId(1L);
//        userPo.setUsername("tommy");
//        userPo.setAge(25);
//        userPo.setPassword(passwordEncoder.encode("password"));
//
//        when(userMapper.selectOne(any())).thenReturn(userPo);
//        HttpSession session = mock(HttpSession.class);
//        when(request.getSession()).thenReturn(session);
//
//
//        UserVo userVo = borrowerService.userLogin(dto, request);
//        assertNotNull(userVo);
//        assertEquals("tommy", userVo.getUsername());
//        verify(session, times(1)).setAttribute(eq(CURRENT_LOGIN_USER), any(UserVo.class));
//    }
//
//    @Test
//    void testUserLogout_Success() {
//
//        when(request.getSession()).thenReturn(session); // 添加这行
//        when(session.getAttribute(CURRENT_LOGIN_USER)).thenReturn(null);;
//
//        Boolean result = borrowerService.userLogout(request);
//        assertTrue(result);
//        verify(session, times(1)).removeAttribute(CURRENT_LOGIN_USER);
//        verify(session, times(1)).getAttribute(CURRENT_LOGIN_USER);
//    }
//
//    @Test
//    void testUpdateUser_Success() {
//        UpdateDto dto = new UpdateDto();
//        dto.setId(1L);
//        dto.setUsername("newName");
//
//        UserPo existing = new UserPo();
//        existing.setId(1L);
//        existing.setUsername("oldName");
//
//        when(userMapper.selectById(1L)).thenReturn(existing);
//
//        UserVo updated = borrowerService.updateUser(dto);
//        assertEquals("newName", updated.getUsername());
//        verify(userMapper, times(1)).updateById(any(UserPo.class));
//    }
//
//    @Test
//    void testDeactivateUser_Success() {
//        DeleteDto dto = new DeleteDto();
//        dto.setId(1L);
//
//        UserPo userPo = new UserPo();
//        userPo.setId(1L);
//        userPo.setIsDelete(0);
//
//        when(userMapper.selectById(1L)).thenReturn(userPo);
//
//        Boolean result = borrowerService.deactivateUser(dto);
//        assertTrue(result);
//        verify(userMapper, times(1)).updateById(any(UserPo.class));
//    }
//
//    @Test
//    void testUserRegister_UserExists() {
//        RegisterDto dto = new RegisterDto();
//        dto.setPhone("123");
//
//        when(userMapper.selectOne(any())).thenReturn(new UserPo());
//
//        Exception exception = assertThrows(RuntimeException.class,
//                () -> borrowerService.userRegister(dto));
//        assertTrue(exception.getMessage().contains("User already exists"));
//    }
//
//    @Test
//    void testUserLogin_UserNotFound() {
//        lenient().when(request.getSession()).thenReturn(session); // 添加这行
//
//        LoginDto dto = new LoginDto();
//        dto.setUsername("nonexistent");
//
//        when(userMapper.selectOne(any())).thenReturn(null);
//
//        Exception exception = assertThrows(RuntimeException.class,
//                () -> borrowerService.userLogin(dto, request));
//        assertTrue(exception.getMessage().contains("Username not exists"));
//    }
//
//    @Test
//    void testUserLogin_PasswordIncorrect() {
//        when(request.getSession()).thenReturn(session); // 添加这行
//
//        LoginDto dto = new LoginDto();
//        dto.setUsername("tommy");
//        dto.setPassword("wrongpassword");
//
//        UserPo userPo = new UserPo();
//        userPo.setUsername("tommy");
//        userPo.setPassword(passwordEncoder.encode("password"));
//
//        when(userMapper.selectOne(any())).thenReturn(userPo);
//
//        Exception exception = assertThrows(RuntimeException.class,
//                () -> borrowerService.userLogin(dto, request));
//        assertEquals("Password is incorrect", exception.getMessage()); // 使用 assertEquals
//    }
//}