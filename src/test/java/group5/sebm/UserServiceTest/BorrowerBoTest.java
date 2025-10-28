package group5.sebm.UserServiceTest;

import group5.sebm.User.service.bo.Borrower;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BorrowerBoTest {

    @Test
    void testNoArgsConstructorAndSettersGetters() {
        Borrower borrower = new Borrower();

        borrower.setOverdueTimes(2);
        borrower.setBorrowedDeviceCount(3);
        borrower.setMaxBorrowedDeviceCount(5);
        borrower.setMaxOverdueTimes(2);
        borrower.setReserveDeviceCount(1);
        borrower.setMaxReserveDeviceCount(3);

        assertEquals(2, borrower.getOverdueTimes());
        assertEquals(3, borrower.getBorrowedDeviceCount());
        assertEquals(5, borrower.getMaxBorrowedDeviceCount());
        assertEquals(2, borrower.getMaxOverdueTimes());
        assertEquals(1, borrower.getReserveDeviceCount());
        assertEquals(3, borrower.getMaxReserveDeviceCount());
    }

    @Test
    void testAllArgsConstructor() {
        Borrower borrower = new Borrower(2, 3, 5, 2, 1, 3);
        assertEquals(3, borrower.getBorrowedDeviceCount());
        assertEquals(5, borrower.getMaxBorrowedDeviceCount());
    }

    @Test
    void testUpdateBorrowedCountPositive() {
        Borrower borrower = new Borrower();
        borrower.setBorrowedDeviceCount(2);
        borrower.updateBorrowedCount(3);
        assertEquals(5, borrower.getBorrowedDeviceCount());
    }

    @Test
    void testUpdateBorrowedCountNegative() {
        Borrower borrower = new Borrower();
        borrower.setBorrowedDeviceCount(5);
        borrower.updateBorrowedCount(-3);
        assertEquals(2, borrower.getBorrowedDeviceCount());
    }

    @Test
    void testUpdateBorrowedCountNegativeBeyondZero() {
        Borrower borrower = new Borrower();
        borrower.setBorrowedDeviceCount(2);
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            borrower.updateBorrowedCount(-3);
        });
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
        assertEquals("Borrowed device count cannot be negative", exception.getMessage());
    }

    @Test
    void testUpdateBorrowedCountNullDelta() {
        Borrower borrower = new Borrower();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            borrower.updateBorrowedCount(null);
        });
        assertEquals("Borrowed count delta cannot be null", exception.getMessage());
    }

    @Test
    void testUpdateBorrowedCountNullCurrentCount() {
        Borrower borrower = new Borrower();
        borrower.setBorrowedDeviceCount(null);
        borrower.updateBorrowedCount(3);
        assertEquals(3, borrower.getBorrowedDeviceCount());
    }
}
