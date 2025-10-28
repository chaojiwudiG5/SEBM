package group5.sebm.aop;

import group5.sebm.annotation.AuthCheck;
import group5.sebm.common.dto.UserDto;
import group5.sebm.common.enums.UserRoleEnum;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.User.service.UserServiceInterface.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AopTest {

  @Mock
  private UserService userService;

  @Mock
  private ProceedingJoinPoint joinPoint;

  @Mock
  private AuthCheck authCheck;

  @Mock
  private HttpServletRequest request;

  private AuthInterceptor interceptor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    interceptor = new AuthInterceptor(userService);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  /**
   * 场景1：未登录（应抛出 NOT_LOGIN_ERROR）
   */
  @Test
  void testNotLoggedIn_shouldThrowNotLoginError() throws Throwable {
    when(userService.getCurrentUserDtoFromHttp(request)).thenReturn(null);

    // mock 枚举返回任意角色
    UserRoleEnum fakeRole = mock(UserRoleEnum.class);
    when(fakeRole.getCode()).thenReturn(UserRoleEnum.USER.getCode());
    when(authCheck.mustRole()).thenReturn(fakeRole);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> interceptor.doInterceptor(joinPoint, authCheck));
    assertEquals(ErrorCode.NOT_LOGIN_ERROR.getCode(), ex.getCode());
  }

  /**
   * 场景2：无需权限（mustRoleCode < 0） -> 正常放行
   */
  @Test
  void testNoPermissionRequired_shouldProceed() throws Throwable {
    // 模拟无需权限：mustRoleCode = -1
    UserRoleEnum fakeRole = mock(UserRoleEnum.class);
    when(fakeRole.getCode()).thenReturn(-1);
    when(authCheck.mustRole()).thenReturn(fakeRole);

    when(userService.getCurrentUserDtoFromHttp(request)).thenReturn(new UserDto());
    when(joinPoint.proceed()).thenReturn("success");

    Object result = interceptor.doInterceptor(joinPoint, authCheck);

    verify(joinPoint, times(1)).proceed();
    assertEquals("success", result);
  }

  /**
   * 场景3：角色不匹配（应抛出 NO_AUTH_ERROR）
   */
  @Test
  void testRoleMismatch_shouldThrowNoAuthError() throws Throwable {
    UserDto user = new UserDto();
    user.setUserRole(UserRoleEnum.USER.getCode());
    when(userService.getCurrentUserDtoFromHttp(request)).thenReturn(user);

    // mustRole = TECHNICIAN（与 USER 不同）
    UserRoleEnum fakeRole = mock(UserRoleEnum.class);
    when(fakeRole.getCode()).thenReturn(UserRoleEnum.TECHNICIAN.getCode());
    when(authCheck.mustRole()).thenReturn(fakeRole);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> interceptor.doInterceptor(joinPoint, authCheck));
    assertEquals(ErrorCode.NO_AUTH_ERROR.getCode(), ex.getCode());
  }

  /**
   * 场景4：必须是管理员，但当前不是 -> 抛出 NO_AUTH_ERROR
   */
  @Test
  void testMustBeAdminButNotAdmin_shouldThrowNoAuthError() throws Throwable {
    UserDto user = new UserDto();
    user.setUserRole(UserRoleEnum.USER.getCode());
    when(userService.getCurrentUserDtoFromHttp(request)).thenReturn(user);

    UserRoleEnum fakeRole = mock(UserRoleEnum.class);
    when(fakeRole.getCode()).thenReturn(UserRoleEnum.ADMIN.getCode());
    when(authCheck.mustRole()).thenReturn(fakeRole);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> interceptor.doInterceptor(joinPoint, authCheck));
    assertEquals(ErrorCode.NO_AUTH_ERROR.getCode(), ex.getCode());
  }

  /**
   * 场景5：管理员访问管理员接口 -> 正常放行
   */
  @Test
  void testValidAdmin_shouldProceed() throws Throwable {
    UserDto user = new UserDto();
    user.setUserRole(UserRoleEnum.ADMIN.getCode());
    when(userService.getCurrentUserDtoFromHttp(request)).thenReturn(user);

    UserRoleEnum fakeRole = mock(UserRoleEnum.class);
    when(fakeRole.getCode()).thenReturn(UserRoleEnum.ADMIN.getCode());
    when(authCheck.mustRole()).thenReturn(fakeRole);

    when(joinPoint.proceed()).thenReturn("ok");

    Object result = interceptor.doInterceptor(joinPoint, authCheck);

    verify(joinPoint, times(1)).proceed();
    assertEquals("ok", result);
  }
}
