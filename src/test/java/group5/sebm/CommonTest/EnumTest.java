package group5.sebm.CommonTest;

import group5.sebm.common.enums.BorrowStatusEnum;
import group5.sebm.common.enums.DeviceStatusEnum;
import group5.sebm.common.enums.ReservationStatusEnum;
import group5.sebm.common.enums.UserRoleEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumTest {

    // ==================== BorrowStatusEnum ====================
    @Test
    void testBorrowStatusEnumValues() {
        assertEquals(0, BorrowStatusEnum.BORROWED.getCode());
        assertEquals("Borrowed", BorrowStatusEnum.BORROWED.getDescription());

        assertEquals(1, BorrowStatusEnum.RETURNED.getCode());
        assertEquals("Returned", BorrowStatusEnum.RETURNED.getDescription());

        assertEquals(2, BorrowStatusEnum.OVERDUE.getCode());
        assertEquals("Overdue", BorrowStatusEnum.OVERDUE.getDescription());
    }

    @Test
    void testBorrowStatusEnumFromCode() {
        assertEquals(BorrowStatusEnum.BORROWED, BorrowStatusEnum.fromCode(0));
        assertEquals(BorrowStatusEnum.RETURNED, BorrowStatusEnum.fromCode(1));
        assertEquals(BorrowStatusEnum.OVERDUE, BorrowStatusEnum.fromCode(2));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> BorrowStatusEnum.fromCode(99));
        assertTrue(e.getMessage().contains("Unknown borrow status code"));
    }

    // ==================== DeviceStatusEnum ====================
    @Test
    void testDeviceStatusEnumValues() {
        assertEquals(0, DeviceStatusEnum.AVAILABLE.getCode());
        assertEquals("Available", DeviceStatusEnum.AVAILABLE.getDescription());

        assertEquals(1, DeviceStatusEnum.BORROWED.getCode());
        assertEquals("Borrowed", DeviceStatusEnum.BORROWED.getDescription());

        assertEquals(2, DeviceStatusEnum.MAINTENANCE.getCode());
        assertEquals("Maintenance", DeviceStatusEnum.MAINTENANCE.getDescription());

        assertEquals(3, DeviceStatusEnum.BROKEN.getCode());
        assertEquals("Broken", DeviceStatusEnum.BROKEN.getDescription());
    }

    @Test
    void testDeviceStatusEnumFromCode() {
        assertEquals(DeviceStatusEnum.AVAILABLE, DeviceStatusEnum.fromCode(0));
        assertEquals(DeviceStatusEnum.BORROWED, DeviceStatusEnum.fromCode(1));
        assertEquals(DeviceStatusEnum.MAINTENANCE, DeviceStatusEnum.fromCode(2));
        assertEquals(DeviceStatusEnum.BROKEN, DeviceStatusEnum.fromCode(3));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> DeviceStatusEnum.fromCode(999));
        assertTrue(e.getMessage().contains("Invalid DeviceStatus code"));
    }

    // ==================== ReservationStatusEnum ====================
    @Test
    void testReservationStatusEnumValues() {
        assertEquals(0, ReservationStatusEnum.NOT_CONFIRMED.getCode());
        assertEquals("Not Confirmed", ReservationStatusEnum.NOT_CONFIRMED.getDescription());

        assertEquals(1, ReservationStatusEnum.CONFIRMED.getCode());
        assertEquals("Confirmed", ReservationStatusEnum.CONFIRMED.getDescription());

        assertEquals(2, ReservationStatusEnum.CANCELED.getCode());
        assertEquals("Canceled", ReservationStatusEnum.CANCELED.getDescription());

        assertEquals(3, ReservationStatusEnum.OVERDUE.getCode());
        assertEquals("Overdue", ReservationStatusEnum.OVERDUE.getDescription());
    }

    @Test
    void testReservationStatusEnumFromCode() {
        assertEquals(ReservationStatusEnum.NOT_CONFIRMED, ReservationStatusEnum.fromCode(0));
        assertEquals(ReservationStatusEnum.CONFIRMED, ReservationStatusEnum.fromCode(1));
        assertEquals(ReservationStatusEnum.CANCELED, ReservationStatusEnum.fromCode(2));
        assertEquals(ReservationStatusEnum.OVERDUE, ReservationStatusEnum.fromCode(3));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> ReservationStatusEnum.fromCode(999));
        assertTrue(e.getMessage().contains("Invalid DeviceStatus code"));
    }

    // ==================== UserRoleEnum ====================
    @Test
    void testUserRoleEnumValues() {
        assertEquals(0, UserRoleEnum.USER.getCode());
        assertEquals("User", UserRoleEnum.USER.getDescription());

        assertEquals(1, UserRoleEnum.ADMIN.getCode());
        assertEquals("Admin", UserRoleEnum.ADMIN.getDescription());

        assertEquals(2, UserRoleEnum.TECHNICIAN.getCode());
        assertEquals("Technician", UserRoleEnum.TECHNICIAN.getDescription());
    }

    @Test
    void testUserRoleEnumFromCode() {
        assertEquals(UserRoleEnum.USER, UserRoleEnum.fromCode(0));
        assertEquals(UserRoleEnum.ADMIN, UserRoleEnum.fromCode(1));
        assertEquals(UserRoleEnum.TECHNICIAN, UserRoleEnum.fromCode(2));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> UserRoleEnum.fromCode(9));
        assertTrue(e.getMessage().contains("Invalid UserRole code"));
    }
}
