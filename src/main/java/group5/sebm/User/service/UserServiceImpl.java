package group5.sebm.User.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.LoginDto;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.User.controller.dto.RegisterDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.dao.UserMapper;
import group5.sebm.User.entity.BorrowerInfoPo;
import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.Info.BorrowerInfoService;
import group5.sebm.User.service.strategy.UserInfoStrategy;
import group5.sebm.User.service.strategy.UserInfoStrategyFactory;
import group5.sebm.User.service.bo.User;
import group5.sebm.common.dto.User.UserInfoDto;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.User.service.bo.Borrower;
import group5.sebm.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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

  private final UserInfoStrategyFactory userInfoStrategyFactory;

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
    
    UserInfoStrategy strategy = userInfoStrategyFactory.getStrategy(userPo.getUserRole());
    return strategy.buildUserVo(userPo);
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
    
    UserInfoStrategy strategy = userInfoStrategyFactory.getStrategy(userPo.getUserRole());
    return strategy.buildUserInfoDto(userPo);
  }

  /**
   * 注册用户(其实只有借用者能注册)
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
    UserInfoStrategy strategy = userInfoStrategyFactory.getStrategy(userPo.getUserRole());
    UserVo userVo = strategy.buildUserVo(userPo);
    strategy.setToken(userVo, token);
    
    return userVo;
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
    //1. check if user exists
    UserPo userPo = baseMapper.selectById(userId);
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");
    //2. update user information
    UserPo newUserPo = new UserPo();
    newUserPo.setId(userId);
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

  /**
   * 删除用户
   *
   * @param deleteDto 用户id
   * @return 是否删除成功
   */
  public Boolean deleteBorrower(DeleteDto deleteDto) {
    //1. check if user exists
    UserPo userPo = baseMapper.selectById(deleteDto.getId());
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");
    //2. delete user from database
    try {
      baseMapper.deleteById(deleteDto.getId());
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Delete failed");
    }
    return true;
  }

  /**
   * 批量删除用户
   */
  @Override
  public Boolean deleteBorrowers(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "id list is empty");
    }
    try {
      baseMapper.deleteByIds(ids);
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Batch delete failed");
    }
    return true;
  }

  /**
   * 获取所有用户
   *
   * @param pageDto 分页信息
   * @return 用户列表
   */
  public Page<UserVo> getAllBorrowers(PageDto pageDto) {
    // 1. 创建分页对象
    Page<UserPo> page = new Page<>(pageDto.getPageNumber(), pageDto.getPageSize());

    // 2. 执行分页查询
    Page<UserPo> userPage = baseMapper.selectPage(page, new QueryWrapper<>());

    // 3. 将 PO 转 VO
    List<UserVo> voList = userPage.getRecords().stream()
        .map(po -> {
          UserVo vo = new UserVo();
          BeanUtils.copyProperties(po, vo);
          return vo;
        })
        .collect(Collectors.toList());


    // 4. 将 VO 列表放回 Page 对象
    Page<UserVo> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(),
        userPage.getTotal());
    resultPage.setRecords(voList);

    return resultPage;
  }
}
