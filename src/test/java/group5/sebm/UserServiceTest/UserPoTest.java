package group5.sebm.UserServiceTest;

import group5.sebm.User.entity.UserPo;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserPoTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        // 使用无参构造
        UserPo user = new UserPo();

        // 测试 setter
        user.setId(1L);
        user.setUsername("Tom");
        user.setPassword("123456");
        user.setEmail("tom@example.com");
        user.setPhone("1234567890");
        user.setGender(1);
        user.setAvatarUrl("avatar.png");
        user.setUserRole(0);
        user.setIsDelete(0);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setUserStatus(0);
        user.setAge(25);
        user.setLevel(1);
        user.setOverdueTimes(0);
        user.setBorrowedDeviceCount(2);
        user.setMaxBorrowedDeviceCount(5);
        user.setMaxOverdueTimes(3);

        // 测试 getter
        assertEquals(1L, user.getId());
        assertEquals("Tom", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals("tom@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhone());
        assertEquals(1, user.getGender());
        assertEquals("avatar.png", user.getAvatarUrl());
        assertEquals(0, user.getUserRole());
        assertEquals(0, user.getIsDelete());
        assertEquals(now, user.getCreateTime());
        assertEquals(now, user.getUpdateTime());
        assertEquals(0, user.getUserStatus());
        assertEquals(25, user.getAge());
        assertEquals(1, user.getLevel());
        assertEquals(0, user.getOverdueTimes());
        assertEquals(2, user.getBorrowedDeviceCount());
        assertEquals(5, user.getMaxBorrowedDeviceCount());
        assertEquals(3, user.getMaxOverdueTimes());
    }

    @Test
    void testAllArgsConstructor() {
        Date now = new Date();
        UserPo user = new UserPo(
                1L, "Tom", "123456", "tom@example.com", "1234567890",
                1, "avatar.png", 0, 0, now, now, 0,
                25, 1, 0, 2, 5, 3
        );

        assertEquals("Tom", user.getUsername());
        assertEquals("123456", user.getPassword());
        assertEquals("tom@example.com", user.getEmail());
        assertEquals("avatar.png", user.getAvatarUrl());
        assertEquals(5, user.getMaxBorrowedDeviceCount());
    }

    @Test
    void testBuilder() {
        Date now = new Date();
        UserPo user = UserPo.builder()
                .id(1L)
                .username("Tom")
                .password("123456")
                .email("tom@example.com")
                .phone("1234567890")
                .gender(1)
                .avatarUrl("avatar.png")
                .userRole(0)
                .isDelete(0)
                .createTime(now)
                .updateTime(now)
                .userStatus(0)
                .age(25)
                .level(1)
                .overdueTimes(0)
                .borrowedDeviceCount(2)
                .maxBorrowedDeviceCount(5)
                .maxOverdueTimes(3)
                .build();

        assertNotNull(user);
        assertEquals("Tom", user.getUsername());
        assertEquals(5, user.getMaxBorrowedDeviceCount());
    }
}
