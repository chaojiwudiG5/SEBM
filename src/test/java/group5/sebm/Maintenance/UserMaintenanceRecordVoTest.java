package group5.sebm.Maintenance;

import group5.sebm.Maintenance.controller.vo.UserMaintenanceRecordVo;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserMaintenanceRecordVoTest {

    @Test
    void testGetterAndSetter() {
        UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo();

        vo.setId(1L);
        vo.setDeviceName("Device A");
        vo.setUserId(10L);
        vo.setDescription("description");
        vo.setImage("http://image.url");
        vo.setStatus(0);
        Date now = new Date();
        vo.setCreateTime(now);
        vo.setUpdateTime(now);

        assertEquals(1L, vo.getId());
        assertEquals("Device A", vo.getDeviceName());
        assertEquals(10L, vo.getUserId());
        assertEquals("description", vo.getDescription());
        assertEquals("http://image.url", vo.getImage());
        assertEquals(0, vo.getStatus());
        assertEquals(now, vo.getCreateTime());
        assertEquals(now, vo.getUpdateTime());
    }
}
