package group5.sebm.UserServiceTest;

import group5.sebm.User.service.bo.Manager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerBoTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        Manager manager = new Manager();
        manager.setIsDelete(true);

        assertTrue(manager.getIsDelete());
        manager.setIsDelete(false);
        assertFalse(manager.getIsDelete());
    }

    @Test
    void testAllArgsConstructor() {
        Manager manager = new Manager(false);
        assertFalse(manager.getIsDelete());

        Manager manager2 = new Manager(true);
        assertTrue(manager2.getIsDelete());
    }
}
