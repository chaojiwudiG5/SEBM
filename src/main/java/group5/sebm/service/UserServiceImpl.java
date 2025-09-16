package group5.sebm.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.controller.dto.UserDto;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.dao.UserMapper;
import group5.sebm.entity.UserPO;
import group5.sebm.service.bo.User;
import group5.sebm.service.services.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * @author Luoimo
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-09-16 13:06:33
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPO>
    implements UserService {

  @Resource
  private UserMapper userMapper;

  private User poToBo(UserPO po) {
    User bo = new User();
    bo.setId(po.getId());
    bo.setUsername(po.getUsername());
    bo.setPassword(po.getPassword());
    bo.setAge(po.getAge());
    return bo;
  }

  private UserVo boToVo(User bo) {
    return new UserVo(bo.getId(), bo.getUsername(), bo.getAge());
  }

  private UserVo poToVo(UserPO po) {
    return new UserVo(po.getId(), po.getUsername(), po.getAge());
  }

  @Override
  public List<UserVo> getAllUsers() {
    Wrapper queryWrapper = new QueryWrapper();
    List<UserPO> userPOList = userMapper.selectList(queryWrapper);
    List<UserVo> userVoList = userPOList.stream().map(this::poToVo).toList();
    return userVoList;
  }

  @Override
  public UserVo getDiscountUserById(Long id) {
    return null;
  }

  //TODO
  @Override
  public User getLoginUser(HttpServletRequest request) {
    return null;
  }

  @Override
  public UserVo addUser(UserDto userDto) {
    UserPO userPO = new UserPO();
    Long id = userDto.getId();
    String username = userDto.getUsername();
    String password = userDto.getPassword();
    Integer age = userDto.getAge();
    userPO.setId(id);
    userPO.setUsername(username);
    userPO.setPassword(password);
    userPO.setAge(age);
    userMapper.insert(userPO);
    return poToVo(userPO);
  }
}




