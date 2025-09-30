package group5.sebm.aop;

import group5.sebm.annotation.AuthCheck;
import group5.sebm.common.dto.UserDto;
import group5.sebm.common.enums.UserRoleEnum;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.User.service.UserServiceInterface.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@AllArgsConstructor

public class AuthInterceptor {

  @Resource
  private UserService userServiceImpl;

  /**
   * 执行拦截
   *
   * @param joinPoint 切入点
   * @param authCheck 权限校验注解
   */
  @Around("@annotation(authCheck)")
  public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
    int mustRoleCode = authCheck.mustRole().getCode();
    RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

    // 当前登录用户
    UserDto loginUser = userServiceImpl.getCurrentUserDto(request);
    ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

    // 不需要权限，放行
    if (mustRoleCode < 0) {
      return joinPoint.proceed();
    }

    // 当前用户角色
    UserRoleEnum userRoleEnum = UserRoleEnum.fromCode(loginUser.getUserRole());

    // 没有权限，拒绝
    ThrowUtils.throwIf(userRoleEnum.getCode() != mustRoleCode, ErrorCode.NO_AUTH_ERROR);

    // 权限校验：必须管理员，但当前用户不是管理员
    if (mustRoleCode == UserRoleEnum.ADMIN.getCode() && userRoleEnum != UserRoleEnum.ADMIN) {
      throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
    }

    // 通过权限校验，放行
    return joinPoint.proceed();
  }
}
