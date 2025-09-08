package group12.sebm.service;

import group12.sebm.dao.UserDao;
import group12.sebm.controller.dto.UserDto;
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

    public UserDto getUserById(Integer id) {
        UserPo po = userDao.findById(id);
        if (po == null) return null;
        return new UserDto(po.getId(), po.getUsername(), null);
    }

    public List<UserDto> getAllUsers() {
        Collection<UserPo> pos = userDao.findAll();
        return pos.stream()
                .map(po -> new UserDto(po.getId(), po.getUsername(), null))
                .collect(Collectors.toList());
    }
}

