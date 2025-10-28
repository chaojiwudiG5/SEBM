package group5.sebm.UserServiceTest;

import group5.sebm.User.service.bo.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserBoTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        User user = new User();

        // 测试字段赋值和获取
        user.setId(1L);
        assertEquals(1L, user.getId());

        user.setUsername("Tommy");
        assertEquals("Tommy", user.getUsername());

        user.setPassword("pass123");
        assertEquals("pass123", user.getPassword());

        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());

        user.setPhone("1234567890");
        assertEquals("1234567890", user.getPhone());

        user.setGender(1);
        assertEquals(1, user.getGender());

        user.setAvatarUrl("avatar.png");
        assertEquals("avatar.png", user.getAvatarUrl());

        user.setUserRole(1);
        assertEquals(1, user.getUserRole());

        user.setIsDelete(true); // 特别测试 isDelete
        assertTrue(user.getIsDelete());

        Date now = new Date();
        user.setUpdateTime(now);
        assertEquals(now, user.getUpdateTime());

        user.setCreateTime(now);
        assertEquals(now, user.getCreateTime());

        user.setUserStatus(0);
        assertEquals(0, user.getUserStatus());

        user.setAge(25);
        assertEquals(25, user.getAge());

        user.setLevel(3);
        assertEquals(3, user.getLevel());
    }

    @Test
    void testAllArgsConstructor() {
        Date now = new Date();
        User user = new User(
                1L, "Tommy", "pass123", "test@example.com", "1234567890", 1,
                "avatar.png", 1, true, now, now, 0, 25, 3
        );

        assertEquals(true, user.getIsDelete()); // 测试 isDelete
        assertEquals("Tommy", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testValidateTwicePassword() {
        User user = new User();

        assertTrue(user.validateTwicePassword("abc123", "abc123"));
        assertFalse(user.validateTwicePassword("abc123", "ABC123"));
        assertFalse(user.validateTwicePassword(null, "abc123"));
    }

    @Test
    void testValidatePasswordWithBCrypt() {
        User user = new User();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String raw = "mypassword";
        String encoded = encoder.encode(raw);

        assertTrue(user.validatePassword("mypassword", encoded, encoder));
        assertFalse(user.validatePassword("wrongpassword", encoded, encoder));
    }
}
