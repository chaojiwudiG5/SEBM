package group5.sebm.User.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.LoginDto;
import group5.sebm.User.controller.dto.RegisterDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.vo.AdminVo;
import group5.sebm.User.controller.vo.BorrowerVo;
import group5.sebm.User.controller.vo.MechanicVo;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.dao.UserMapper;
import group5.sebm.User.entity.AdminInfoPo;
import group5.sebm.User.entity.BorrowerInfoPo;
import group5.sebm.User.entity.MechanicInfoPo;
import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.Info.AdminInfoService;
import group5.sebm.User.service.Info.BorrowerInfoService;
import group5.sebm.User.service.Info.MechanicInfoService;
import group5.sebm.User.service.bo.User;
import group5.sebm.common.dto.User.AdminInfoDto;
import group5.sebm.common.dto.User.MechanicInfoDto;
import group5.sebm.common.dto.User.UserInfoDto;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.GlobalExceptionHandler;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.User.service.bo.Borrower;
import group5.sebm.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import group5.sebm.common.dto.User.BorrowerInfoDto;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author
 * @description 用户服务实现
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPo> implements UserService {

  protected final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  private final BorrowerInfoService borrowerInfoService;

  private final AdminInfoService adminInfoService;

  private final MechanicInfoService mechanicInfoService;

  /**
   * 获取当前登录用户（给前端使用）
   *
   * @param request http 请求
   * @return 当前登录用户
   */
  @Override
  public UserVo getCurrentUser(HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "Not login");
    UserPo userPo = baseMapper.selectById(userId);
    //TODO:添加其他角色的返回值
    switch (userPo.getUserRole()) {
      case 0:
        BorrowerVo borrowerVo = new BorrowerVo();
        BorrowerInfoPo borrowerInfoPo = borrowerInfoService.getOne(
            new QueryWrapper<BorrowerInfoPo>().eq("userId", userPo.getId()));
        BeanUtils.copyProperties(userPo, borrowerVo);
        BeanUtils.copyProperties(borrowerInfoPo, borrowerVo);
        return borrowerVo;
      case 1:
        AdminVo adminVo = new AdminVo();
        AdminInfoPo adminInfoPo = adminInfoService.getOne(
            new QueryWrapper<AdminInfoPo>().eq("userId", userPo.getId()));
        BeanUtils.copyProperties(userPo, adminVo);
        BeanUtils.copyProperties(adminInfoPo, adminVo);
        return adminVo;
      case 2:
        MechanicVo mechanicVo = new MechanicVo();
        MechanicInfoPo mechanicInfoPo = mechanicInfoService.getOne(
            new QueryWrapper<MechanicInfoPo>().eq("userId", userPo.getId()));
        BeanUtils.copyProperties(userPo, mechanicVo);
        BeanUtils.copyProperties(mechanicInfoPo, mechanicVo);
        return mechanicVo;

      default:
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Unknown user role");
    }
  }

  /**
   * 获取当前登录用户（给内部服务使用）
   *
   * @param request http 请求
   * @return 当前登录用户
   */
  @Override
  public UserInfoDto getCurrentUserDto(HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    ThrowUtils.throwIf(userId == null, ErrorCode.NOT_LOGIN_ERROR, "Not login");
    UserPo userPo = baseMapper.selectById(userId);
    //TODO:添加其他角色的返回值
    switch (userPo.getUserRole()) {
      case 0:
        BorrowerInfoPo borrowerInfoPo = borrowerInfoService.getOne(
            new QueryWrapper<BorrowerInfoPo>().eq("userId", userPo.getId()));
        BorrowerInfoDto borrowerInfoDto = new BorrowerInfoDto();
        BeanUtils.copyProperties(borrowerInfoPo, borrowerInfoDto);
        BeanUtils.copyProperties(userPo, borrowerInfoDto);
        return borrowerInfoDto;
      case 1:
        AdminInfoPo adminInfoPo = adminInfoService.getOne(
            new QueryWrapper<AdminInfoPo>().eq("userId", userPo.getId()));
        AdminInfoDto adminInfoDto = new AdminInfoDto();
        BeanUtils.copyProperties(adminInfoPo, adminInfoDto);
        BeanUtils.copyProperties(userPo, adminInfoDto);
        return adminInfoDto;
      case 2:
        MechanicInfoPo mechanicInfoPo = mechanicInfoService.getOne(
            new QueryWrapper<MechanicInfoPo>().eq("userId", userPo.getId()));
        MechanicInfoDto mechanicInfoDto = new MechanicInfoDto();
        BeanUtils.copyProperties(mechanicInfoPo, mechanicInfoDto);
        BeanUtils.copyProperties(userPo, mechanicInfoDto);
        return mechanicInfoDto;
      default:
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Unknown user role");
    }
  }

  /**
   * 注册用户
   *
   * @param registerDto 用户信息
   * @return 用户id
   */
  //todo:分角色不同返回值
  @Override
  @Transactional(rollbackFor = BusinessException.class)
  public Long userRegister(RegisterDto registerDto) {
    //1. check if user already exists
    UserPo userPo = baseMapper.selectOne(
        new QueryWrapper<UserPo>().eq("phone", registerDto.getPhone()));
    ThrowUtils.throwIf(userPo != null, ErrorCode.NOT_FOUND_ERROR, "User already exists");

    //2. check if checkPassword equals password
    Borrower borrower = new Borrower();
    BeanUtils.copyProperties(registerDto, borrower);
    boolean isPasswordSame = borrower.validateTwicePassword(registerDto.getPassword(),
        registerDto.getCheckPassword());
    ThrowUtils.throwIf(!isPasswordSame, ErrorCode.PARAMS_ERROR, "Passwords do not match");

    //3. create user
    UserPo po = new UserPo();
    BeanUtils.copyProperties(borrower, po);
    po.setUserRole(0);
    po.setPassword(passwordEncoder.encode(registerDto.getPassword()));

    //4. insert user into database
    baseMapper.insert(po);
    BorrowerInfoPo borrowerInfoPo = new BorrowerInfoPo();
    borrowerInfoPo.setUserId(po.getId());
    borrowerInfoService.save(borrowerInfoPo);
    //5. return user id
    return po.getId();
  }

  /**
   * 用户登录(分角色不同返回值)
   *
   * @param loginDto
   * @return
   */
  @Override
  public UserVo userLogin(LoginDto loginDto) {
    // 1. 检查用户是否存在
    UserPo userPo = baseMapper.selectOne(
        new QueryWrapper<UserPo>().eq("username", loginDto.getUsername()));
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "Username not exists");

    // 2. 校验密码
    User user = new User();
    BeanUtils.copyProperties(userPo, user);
    boolean isPasswordCorrect = user.validatePassword(loginDto.getPassword(), userPo.getPassword(),
        passwordEncoder);
    ThrowUtils.throwIf(!isPasswordCorrect, ErrorCode.PASS_ERROR, "Password is incorrect");

    // 3. 生成 JWT token
    String token = JwtUtils.generateToken(userPo.getId());

    // 4. 根据角色的不同返回不同的用户信息
    //TODO:添加其他角色的返回值
    switch (userPo.getUserRole()) {
      case 0:
        BorrowerVo borrowerVo = new BorrowerVo();
        BorrowerInfoPo borrowerInfoPo = borrowerInfoService.getOne(
            new QueryWrapper<BorrowerInfoPo>().eq("userId", userPo.getId()));
        BeanUtils.copyProperties(userPo, borrowerVo);
        BeanUtils.copyProperties(borrowerInfoPo, borrowerVo);
        borrowerVo.setToken(token);
        return borrowerVo;
      case 1:
        AdminVo adminVo = new AdminVo();
        AdminInfoPo adminInfoPo = adminInfoService.getOne(
            new QueryWrapper<AdminInfoPo>().eq("userId", userPo.getId()));
        BeanUtils.copyProperties(userPo, adminVo);
        BeanUtils.copyProperties(adminInfoPo, adminVo);
        adminVo.setToken(token);
        return adminVo;
      case 2:
        MechanicVo mechanicVo = new MechanicVo();
        MechanicInfoPo mechanicInfoPo = mechanicInfoService.getOne(
            new QueryWrapper<MechanicInfoPo>().eq("userId", userPo.getId()));
        BeanUtils.copyProperties(userPo, mechanicVo);
        BeanUtils.copyProperties(mechanicInfoPo, mechanicVo);
        mechanicVo.setToken(token);
        return mechanicVo;
      default:
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Unknown user role");
    }
  }


  /**
   * 更新用户信息
   *
   * @param updateDto 用户信息
   * @return 更新后的用户信息
   */
  @Override
  public UserVo updateUser(UpdateDto updateDto, HttpServletRequest request) {
    //0.check if it is the current login user
    Long userId = (Long) request.getAttribute("userId");
    ThrowUtils.throwIf(!Objects.equals(updateDto.getId(), userId), ErrorCode.NOT_LOGIN_ERROR,
        "Not login");
    //1. check if user exists
    UserPo userPo = baseMapper.selectById(updateDto.getId());
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");
    //2. update user information
    UserPo newUserPo = new UserPo();
    BeanUtils.copyProperties(updateDto, newUserPo);
    baseMapper.updateById(newUserPo);
    //3. return updated user information
    UserVo userVo = new UserVo();
    BeanUtils.copyProperties(newUserPo, userVo);
    //4. return updated user information
    return userVo;
  }

  /**
   * 用户注销
   *
   * @param deleteDto 用户id
   */
  @Override
  public Long deactivateUser(DeleteDto deleteDto) {
    // 1. 检查用户是否存在
    UserPo userPo = baseMapper.selectById(deleteDto.getId());
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");

    // 2. 注销用户
    userPo.setIsDelete(1);
    baseMapper.updateById(userPo);

    // 3. 返回用户id
    return userPo.getId();
  }
}
