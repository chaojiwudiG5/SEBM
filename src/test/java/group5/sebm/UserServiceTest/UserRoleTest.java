package group5.sebm.UserServiceTest;

import group5.sebm.User.enums.UserRoleEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void testGetters() {
        UserRoleEnum user = UserRoleEnum.USER;
        UserRoleEnum admin = UserRoleEnum.ADMIN;

        assertEquals("用户", user.getText());
        assertEquals("user", user.getValue());

        assertEquals("管理员", admin.getText());
        assertEquals("admin", admin.getValue());
    }

    @Test
    void testGetEnumByValue() {
        // 测试有效值
        assertEquals(UserRoleEnum.USER, UserRoleEnum.getEnumByValue("user"));
        assertEquals(UserRoleEnum.ADMIN, UserRoleEnum.getEnumByValue("admin"));

        // 测试无效值
        assertNull(UserRoleEnum.getEnumByValue("unknown"));

        // 测试空值
        assertNull(UserRoleEnum.getEnumByValue(null));
        assertNull(UserRoleEnum.getEnumByValue(""));
    }
}
