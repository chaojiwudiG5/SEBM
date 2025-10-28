package group5.sebm.UserServiceTest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import group5.sebm.User.controller.vo.UserVo;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserVoTest {

    @Test
    void testAllFieldsSettersAndGetters() {
        UserVo userVo = new UserVo();

        userVo.setId(1L);
        userVo.setUsername("testUser");
        userVo.setPassword("secret");
        userVo.setEmail("test@example.com");
        userVo.setPhone("1234567890");
        userVo.setGender(1);
        userVo.setAvatarUrl("avatar.png");
        userVo.setUserRole(0);
        userVo.setUserStatus(0);
        userVo.setIsDelete(1);
        userVo.setAge(25);
        userVo.setLevel(2);
        userVo.setOverdueTimes(0);
        userVo.setBorrowedDeviceCount(1);
        userVo.setMaxBorrowedDeviceCount(5);
        userVo.setMaxOverdueTimes(3);
        userVo.setCreateTime(new Date());
        userVo.setUpdateTime(new Date());
        userVo.setActive(true);
        userVo.setToken("token123");

        assertEquals(1L, userVo.getId());
        assertEquals("testUser", userVo.getUsername());
        assertEquals("secret", userVo.getPassword());
        assertEquals("test@example.com", userVo.getEmail());
        assertEquals("1234567890", userVo.getPhone());
        assertEquals(1, userVo.getGender());
        assertEquals("avatar.png", userVo.getAvatarUrl());
        assertEquals(0, userVo.getUserRole());
        assertEquals(0, userVo.getUserStatus());
        assertEquals(1, userVo.getIsDelete());
        assertEquals(25, userVo.getAge());
        assertEquals(2, userVo.getLevel());
        assertEquals(0, userVo.getOverdueTimes());
        assertEquals(1, userVo.getBorrowedDeviceCount());
        assertEquals(5, userVo.getMaxBorrowedDeviceCount());
        assertEquals(3, userVo.getMaxOverdueTimes());
        assertTrue(userVo.isActive());
        assertEquals("token123", userVo.getToken());
    }

    @Test
    void testBuilder() {
        Date now = new Date();
        UserVo userVo = UserVo.builder()
                .id(2L)
                .username("builderUser")
                .password("builderSecret")
                .createTime(now)
                .updateTime(now)
                .isActive(false)
                .token("builderToken")
                .build();

        assertEquals(2L, userVo.getId());
        assertEquals("builderUser", userVo.getUsername());
        assertEquals("builderSecret", userVo.getPassword());
        assertEquals(now, userVo.getCreateTime());
        assertEquals(now, userVo.getUpdateTime());
        assertFalse(userVo.isActive());
        assertEquals("builderToken", userVo.getToken());
    }

    @Test
    void testJsonIgnoreAnnotation() throws NoSuchFieldException {
        // 验证 password 和 isDelete 字段标记了 @JsonIgnore
        assertTrue(UserVo.class.getDeclaredField("password").isAnnotationPresent(JsonIgnore.class));
        assertTrue(UserVo.class.getDeclaredField("isDelete").isAnnotationPresent(JsonIgnore.class));
    }
}
