package group5.sebm.Maintenance;

import group5.sebm.Maintenance.controller.vo.MechanicanMaintenanceRecordVo;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MechanicanMaintenanceRecordVoTest {

    @Test
    void testGetterAndSetter() {
        MechanicanMaintenanceRecordVo vo = new MechanicanMaintenanceRecordVo();

        vo.setId(1L);
        vo.setDeviceId(100L);
        vo.setUserId(200L);
        vo.setDescription("description");
        vo.setImage("http://image.url");
        vo.setStatus(1);
        Date now = new Date();
        vo.setCreateTime(now);
        vo.setUpdateTime(now);
        vo.setUserMaintenanceRecordId(300L);

        assertEquals(1L, vo.getId());
        assertEquals(100L, vo.getDeviceId());
        assertEquals(200L, vo.getUserId());
        assertEquals("description", vo.getDescription());
        assertEquals("http://image.url", vo.getImage());
        assertEquals(1, vo.getStatus());
        assertEquals(now, vo.getCreateTime());
        assertEquals(now, vo.getUpdateTime());
        assertEquals(300L, vo.getUserMaintenanceRecordId());
    }
}
