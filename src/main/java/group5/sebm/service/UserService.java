package group5.sebm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import group5.sebm.controller.dto.UserDto;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.dao.UserMapper;
import group5.sebm.service.bo.Client;
import group5.sebm.entity.UserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

  private final UserMapper userMapper;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  public UserService(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  private Client poToBo(UserPo po) {
    return new Client(po.getId(), po.getUsername(), po.getPassword(), po.getAge());
  }

  private UserVo boToVo(Client bo) {
    return new UserVo(bo.getId(), bo.getUsername(), bo.getAge());
  }

  private UserVo poToVo(UserPo po) {
    return new UserVo(po.getId(), po.getUsername(), po.getAge());
  }

//  public void createUser(UserVo vo, String password) {
//    UserPo po = new UserPo(vo.getId(), vo.getUsername(), password, vo.getAge());
//    userMapper.save(po);
//  }
  public void UserRegister(UserDto userDto) {
    Client bo = new Client(null, userDto.getUsername(), userDto.getPassword(), userDto.getAge());
    UserPo po = new UserPo(bo.getId(), bo.getUsername(), passwordEncoder.encode(bo.getPassword()), bo.getAge());
    userMapper.insert(po);
  }

  public void login(UserDto dto, HttpSession session) {
    UserPo po = userMapper.selectById(dto.getId());
    if (po == null) throw new IllegalArgumentException("用户不存在");

    if (!passwordEncoder.matches(dto.getPassword(), po.getPassword())) {
      throw new IllegalArgumentException("密码错误");
    }
    session.setAttribute("currentUser", po);
  }

  public void logout(UserDto dto, HttpSession session) {
    UserPo po = userMapper.selectById(dto.getId());
    if (po == null) throw new IllegalArgumentException("用户不存在");

    session.invalidate();
  }

  public void deleteUser(int id) {
    if (userMapper.selectById(id)) {
      userMapper.deleteById(id);
    }
  }

  public void updateUser(UserVo vo, String password) {
    if (userMapper.selectById(vo.getId())) {
      UserPo po = userMapper.selectById(vo.getId()).orElseThrow();
      po.setUsername(vo.getUsername());
      if (password != null && !password.isEmpty()) {
        po.setPassword(password);
      }
      po.setAge(vo.getAge());
      userMapper.save(po);
    }
  }



  public List<UserVo> getAllDiscountUsers() {
    List<UserPo> allUsers = userMapper.selectList(new QueryWrapper<>());
    return allUsers.stream()
            .map(this::poToBo)
            .filter(Client::isokforDiscount)
            .map(this::boToVo)
            .toList();
  }

  public UserVo getDiscountUserById(int id) {
    UserPo po = userMapper.selectById(id).orElse(null);
    if (po == null) return null;
    Client bo = poToBo(po);
    if (bo.isokforDiscount()) {
      return boToVo(bo);
    } else {
      return null;
    }
  }

  public List<UserVo> getAllUsers()
  {
    List<UserPo> allUsers = userMapper.selectList(new QueryWrapper<>());
    return allUsers.stream()
            .map(this::poToVo)
            .collect(Collectors.toList());
  }
}

