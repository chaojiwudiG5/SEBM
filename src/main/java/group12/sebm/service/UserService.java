package group12.sebm.service;

import group12.sebm.controller.vo.UserVo;
import group12.sebm.dao.UserDao;
import group12.sebm.controller.dto.UserDto;
import group12.sebm.service.bo.User;
import group12.sebm.entity.UserPo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
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

    public List<UserVo> getAllUsers() {
        Collection<UserPo> pos = userDao.findAll();
        return pos.stream()
                .map(this::poToVo) // 在Service层完成转换
                .collect(Collectors.toList());
    }

    public UserVo getDiscountUserById(Integer id) {
        UserPo po = userDao.findById(id); // 调用 DAO 获取 PO
        if (po == null) return null;

        User bo = poToBo(this.userDao.findById(id));
        boolean canDiscount = bo.isokforDiscount();
        if(!canDiscount) {
            return null;
        }
        else {
            return boToVo(bo);
        }
    }
}

