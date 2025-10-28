package group5.sebm.UserServiceTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import group5.sebm.User.controller.vo.UserVo;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserVoTest {

    @Test
    void testGetterSetter() {
        UserVo user = new UserVo();

        user.setId(1L);
        user.setUsername("tommy");
        user.setPassword("secret");
        user.setEmail("tommy@example.com");
        user.setPhone("123456789");
        user.setGender(1);
        user.setAvatarUrl("http://avatar.com/img.png");
        user.setUserRole(2);
        user.setUserStatus(1);
        user.setIsDelete(0);
        user.setAge(25);
        user.setLevel(3);
        user.setOverdueTimes(1);
        user.setBorrowedDeviceCount(2);
        user.setMaxBorrowedDeviceCount(5);
        user.setMaxOverdueTimes(3);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setToken("abc123");

        assertEquals(1L, user.getId());
        assertEquals("tommy", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertEquals("tommy@example.com", user.getEmail());
        assertEquals("123456789", user.getPhone());
        assertEquals(1, user.getGender());
        assertEquals("http://avatar.com/img.png", user.getAvatarUrl());
        assertEquals(2, user.getUserRole());
        assertEquals(1, user.getUserStatus());
        assertEquals(0, user.getIsDelete());
        assertEquals(25, user.getAge());
        assertEquals(3, user.getLevel());
        assertEquals(1, user.getOverdueTimes());
        assertEquals(2, user.getBorrowedDeviceCount());
        assertEquals(5, user.getMaxBorrowedDeviceCount());
        assertEquals(3, user.getMaxOverdueTimes());
        assertEquals("abc123", user.getToken());
    }

    @Test
    void testAllArgsConstructor() {
        Date now = new Date();
        UserVo user = new UserVo(
                1L,
                "tommy",
                "secret",
                "tommy@example.com",
                "123456789",
                1,
                "http://avatar.com/img.png",
                2,
                1,
                0,
                25,
                3,
                1,
                2,
                5,
                3,
                now,
                now,
                true,
                "abc123"
        );

        assertEquals("tommy", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertTrue(user.isActive());
    }

    @Test
    void testJsonIgnore() throws Exception {
        UserVo user = new UserVo();
        user.setPassword("secret");
        user.setIsDelete(1);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);

        // password 和 isDelete 应该被忽略
        assertFalse(json.contains("password"));
        assertFalse(json.contains("isDelete"));
    }
}
