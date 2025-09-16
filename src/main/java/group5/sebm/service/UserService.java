package group5.sebm.service;

import group5.sebm.controller.dto.UserDto;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.dao.UserRepository;
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

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
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
//    userRepository.save(po);
//  }
  public void UserRegister(UserDto userDto) {
    Client bo = new Client(null, userDto.getUsername(), userDto.getPassword(), userDto.getAge());
    UserPo po = new UserPo(bo.getId(), bo.getUsername(), passwordEncoder.encode(bo.getPassword()), bo.getAge());
    userRepository.save(po);
  }

  public void login(UserDto dto, HttpSession session) {
    UserPo po = userRepository.findByid(dto.getId())
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    if (!passwordEncoder.matches(dto.getPassword(), po.getPassword())) {
      throw new IllegalArgumentException("密码错误");
    }
    session.setAttribute("currentUser", po);
  }

  public void logout(UserDto dto, HttpSession session) {
    UserPo po = userRepository.findByid(dto.getId())
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

    session.invalidate();
  }

  public void deleteUser(int id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
    }
  }

  public void updateUser(UserVo vo, String password) {
    if (userRepository.existsById(vo.getId())) {
      UserPo po = userRepository.findById(vo.getId()).orElseThrow();
      po.setUsername(vo.getUsername());
      if (password != null && !password.isEmpty()) {
        po.setPassword(password);
      }
      po.setAge(vo.getAge());
      userRepository.save(po);
    }
  }


  public List<UserVo> getAllUsers() {
    return userRepository.findAll()
        .stream()
        .map(this::poToVo)
        .collect(Collectors.toList());
  }

  public UserVo getDiscountUserById(Integer id) {
    return userRepository.findById(id)
        .map(po -> {
          Client bo = poToBo(po);
          return bo.isokforDiscount() ? boToVo(bo) : null;
        })
        .orElse(null);
  }
}

