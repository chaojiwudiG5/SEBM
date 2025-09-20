package group5.sebm.service;

import static group5.sebm.common.constant.UserConstant.CURRENT_LOGIN_USER;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.controller.dto.DeleteDto;
import group5.sebm.controller.dto.PageDto;
import group5.sebm.controller.dto.UserLoginDto;
import group5.sebm.controller.dto.UserRegisterDto;
import group5.sebm.controller.dto.UserUpdateDto;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.dao.UserMapper;
import group5.sebm.entity.UserPo;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.service.bo.Borrower;
import group5.sebm.service.bo.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPo> implements UserService {

  private final UserMapper userMapper;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  public UserServiceImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  private Borrower poToBo(UserPo po) {
    Borrower bo = new Borrower();
    BeanUtils.copyProperties(po, bo);
    return bo;
  }

  private UserVo boToVo(Borrower bo) {
    UserVo vo = new UserVo();
    BeanUtils.copyProperties(bo, vo);
    return vo;
  }

  private UserVo poToVo(UserPo po) {
    UserVo vo = new UserVo();
    BeanUtils.copyProperties(po, vo);
    return vo;
  }

  /**
   * 注册用户
   *
   * @param userRegisterDto 用户信息
   * @return 用户id
   */
  @Override
  public Long userRegister(UserRegisterDto userRegisterDto) {
    //1. check if userid already exists
    UserPo userPo = userMapper.selectOne(
        new QueryWrapper<UserPo>().eq("phone", userRegisterDto.getPhone()));
    ThrowUtils.throwIf(userPo != null, ErrorCode.PARAMS_ERROR, "User already exists");
    //2. check if checkPassword equals password
    User user = new User();
    BeanUtils.copyProperties(userRegisterDto, user);
    boolean isPasswordSame = user.validateTwicePassword(userRegisterDto.getPassword(),
        userRegisterDto.getCheckPassword());
    ThrowUtils.throwIf(!isPasswordSame, ErrorCode.PARAMS_ERROR, "Passwords do not match");
    //3. create user
    UserPo po = new UserPo();
    BeanUtils.copyProperties(user, po);
    po.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
    //4. insert user into database
    userMapper.insert(po);
    //5. return user id
    return po.getId();
  }

  /**
   * 用户登录
   *
   * @param userLoginDto 用户信息
   * @param request      请求
   * @return 用户信息
   */
  @Override
  public UserVo userLogin(UserLoginDto userLoginDto, HttpServletRequest request) {
    //1. check if username exists
    UserPo userPo = userMapper.selectOne(
        new QueryWrapper<UserPo>().eq("username", userLoginDto.getUsername()));
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "Username not exists");
    //2.select password from database,check if password is correct
    User userBo = new User();
    BeanUtils.copyProperties(userPo, userBo);
    boolean isPasswordCorrect = userBo.validatePassword(userLoginDto.getPassword(),
        userPo.getPassword(), passwordEncoder);
    ThrowUtils.throwIf(!isPasswordCorrect, ErrorCode.NOT_FOUND_ERROR, "Password is incorrect");
    //3. set current login user to session
    UserVo userVo = poToVo(userPo);
    request.getSession().setAttribute(CURRENT_LOGIN_USER, userVo);
    //4. return user information
    return userVo;
  }

  @Override
  public Boolean userLogout(HttpServletRequest request) {
    HttpSession session = request.getSession();
    session.removeAttribute(CURRENT_LOGIN_USER);
    ThrowUtils.throwIf(session.getAttribute(CURRENT_LOGIN_USER) != null, ErrorCode.SYSTEM_ERROR,
        "Logout failed");
    return true;
  }

  @Override
  public Boolean deleteUser(DeleteDto deleteDto) {
    //1. check if user exists
    UserPo userPo = userMapper.selectById(deleteDto.getId());
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");
    //2. delete user from database
    try {
      userMapper.deleteById(deleteDto.getId());
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Delete failed");
    }
    return true;
  }

  @Override
  public UserVo updateUser(UserUpdateDto userUpdateDto) {
    //1. check if user exists
    UserPo userPo = userMapper.selectById(userUpdateDto.getId());
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");
    //2. update user information
    UserPo newUserPo = new UserPo();
    BeanUtils.copyProperties(userUpdateDto, newUserPo);
    userMapper.updateById(newUserPo);
    //3. return updated user information
    return poToVo(newUserPo);
  }

//  public List<UserVo> getAllDiscountUsers() {
//    List<UserPo> allUsers = userMapper.selectList(new QueryWrapper<>());
//    return allUsers.stream()
//        .map(this::poToBo)
//        .filter(BorrowerBo::isokforDiscount)
//        .map(this::boToVo)
//        .toList();
//  }
//
//  public UserVo getDiscountUserById(int id) {
//    UserPo po = userMapper.selectById(id).orElse(null);
//    if (po == null) {
//      return null;
//    }
//    BorrowerBo bo = poToBo(po);
//    if (bo.isokforDiscount()) {
//      return boToVo(bo);
//    } else {
//      return null;
//    }
//  }

  public Page<UserVo> getAllUsers(PageDto pageDto) {
    // 1. 创建分页对象
    Page<UserPo> page = new Page<>(pageDto.getPageNumber(), pageDto.getPageSize());

    // 2. 执行分页查询
    Page<UserPo> userPage = userMapper.selectPage(page, new QueryWrapper<>());

    // 3. 将 PO 转 VO
    List<UserVo> voList = userPage.getRecords().stream()
        .map(this::poToVo)
        .collect(Collectors.toList());

    // 4. 将 VO 列表放回 Page 对象
    Page<UserVo> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(),
        userPage.getTotal());
    resultPage.setRecords(voList);

    return resultPage;
  }

  public UserVo getLoginUser(HttpServletRequest request) {
    UserVo userVo = (UserVo) request.getSession().getAttribute(CURRENT_LOGIN_USER);
    return userVo;
  }
}