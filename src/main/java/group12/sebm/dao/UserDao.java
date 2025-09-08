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
        // 初始化模拟数据
        userMap.put(1, new UserPo(1, "Tom", "tom@example.com"));
        userMap.put(2, new UserPo(2, "Alice", "alice@example.com"));
        userMap.put(3, new UserPo(3, "Bob", "bob@example.com"));
    }

    // 根据ID查询
    public UserPo findById(Integer id) {
        return userMap.get(id);
    }

    // 查询所有用户
    public Collection<UserPo> findAll() {
        return userMap.values();
    }
}

