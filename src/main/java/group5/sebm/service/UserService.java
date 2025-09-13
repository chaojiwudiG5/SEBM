package group5.sebm.service;

import group5.sebm.controller.vo.UserVo;
import group5.sebm.dao.UserRepository;
import group5.sebm.service.bo.User;
import group5.sebm.entity.UserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User poToBo(UserPo po) {
        return new User(po.getId(), po.getUsername(), po.getPassword(), po.getAge());
    }

    private UserVo boToVo(User bo) {
        return new UserVo(bo.getId(), bo.getUsername(), bo.getAge());
    }

    private UserVo poToVo(UserPo po) {
        return new UserVo(po.getId(), po.getUsername(), po.getAge());
    }

    public void createUser(UserVo vo, String password) {
        UserPo po = new UserPo(vo.getId(), vo.getUsername(), password, vo.getAge());
        userRepository.save(po);
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
                    User bo = poToBo(po);
                    return bo.isokforDiscount() ? boToVo(bo) : null;
                })
                .orElse(null);
    }
}

