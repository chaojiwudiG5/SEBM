package group5.sebm.User.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.User.controller.dto.LoginDto;
import group5.sebm.User.controller.dto.RegisterDto;
import group5.sebm.User.controller.dto.UpdateDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.dao.UserMapper;
import group5.sebm.User.entity.UserPo;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.User.service.bo.Borrower;
import group5.sebm.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static group5.sebm.common.constant.UserConstant.CURRENT_LOGIN_USER;


/**
 * @author
 * @description 用户服务实现
 */
@Service
@NoArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPo> implements UserService {

  protected final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  /**
   * 获取当前登录用户
   *
   * @param request http 请求
   * @return 当前登录用户
   */
  @Override
  public UserVo getCurrentUser(HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    if (userId == null) {
      ThrowUtils.throwIf(true, ErrorCode.NOT_LOGIN_ERROR, "Not login");
    }
    UserPo userPo = baseMapper.selectById(userId);
    UserVo userVo = new UserVo();
    BeanUtils.copyProperties(userPo, userVo);
    return userVo;
  }

  /**
   * 注册用户
   *
   * @param registerDto 用户信息
   * @return 用户id
   */
  @Override
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
    po.setPassword(passwordEncoder.encode(registerDto.getPassword()));

    //4. insert user into database
    baseMapper.insert(po);
    //5. return user id
    return po.getId();
  }

  @Override
  public UserVo userLogin(LoginDto loginDto) {
    // 1. 检查用户是否存在
    UserPo userPo = baseMapper.selectOne(
        new QueryWrapper<UserPo>().eq("username", loginDto.getUsername()));
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "Username not exists");

    // 2. 校验密码
    Borrower borrower = new Borrower();
    BeanUtils.copyProperties(userPo, borrower);
    boolean isPasswordCorrect = borrower.validatePassword(
        loginDto.getPassword(),
        userPo.getPassword(),
        passwordEncoder
    );
    ThrowUtils.throwIf(!isPasswordCorrect, ErrorCode.PASS_ERROR, "Password is incorrect");

    // 3. 生成 JWT token
    String token = JwtUtils.generateToken(userPo.getId());

    // 4. 封装返回对象
    UserVo userVo = new UserVo();
    BeanUtils.copyProperties(userPo, userVo);
    userVo.setToken(token);
    return userVo;
  }


  /**
   * 更新用户信息
   *
   * @param updateDto 用户信息
   * @return 更新后的用户信息
   */
  @Override
  public UserVo updateUser(UpdateDto updateDto,HttpServletRequest request) {
    //0.check if it is the current login user
    Long userId = (Long) request.getAttribute("userId");
    ThrowUtils.throwIf(updateDto.getId() != userId, ErrorCode.NOT_LOGIN_ERROR, "Not login");
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


  @Override
  public boolean updateBatchById(Collection<UserPo> entityList, int batchSize) {
    return false;
  }

  @Override
  public boolean saveOrUpdate(UserPo entity) {
    return false;
  }

  @Override
  public UserPo getOne(Wrapper<UserPo> queryWrapper, boolean throwEx) {
    return null;
  }

  @Override
  public Optional<UserPo> getOneOpt(Wrapper<UserPo> queryWrapper, boolean throwEx) {
    return Optional.empty();
  }

  @Override
  public Map<String, Object> getMap(Wrapper<UserPo> queryWrapper) {
    return Map.of();
  }

  @Override
  public <V> V getObj(Wrapper<UserPo> queryWrapper, Function<? super Object, V> mapper) {
    return null;
  }

  @Override
  public Class<UserPo> getEntityClass() {
    return null;
  }
}
