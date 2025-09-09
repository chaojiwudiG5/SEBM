package group12.sebm.dao;

import group12.sebm.entity.UserPo;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserDao {

    private final Map<Integer, UserPo> userMap = new HashMap<>();

    public UserDao() {
        userMap.put(1, new UserPo(1, "Tom", "123456", 10));
        userMap.put(2, new UserPo(2, "Alice", "alicepwd", 60));
        userMap.put(3, new UserPo(3, "Bob", "bobpwd", 25));
    }

    public UserPo findById(Integer id) {
        return userMap.get(id);
    }

    public Collection<UserPo> findAll() {
        return userMap.values();
    }
}

