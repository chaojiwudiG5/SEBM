package group5.sebm.Maintenance;

import group5.sebm.Maintenance.entity.MechanicanMaintenanceRecordPo;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MechanicanMaintenanceRecordPoTest {

    @Test
    void testGetterAndSetter() {
        MechanicanMaintenanceRecordPo po = new MechanicanMaintenanceRecordPo();

        po.setId(1L);
        po.setDeviceId(100L);
        po.setUserId(200L);
        po.setDescription("description");
        po.setImage("http://image.url");
        po.setStatus(1);
        po.setIsDelete(0);
        Date now = new Date();
        po.setCreateTime(now);
        po.setUpdateTime(now);
        po.setUserMaintenanceRecordId(300L);

        assertEquals(1L, po.getId());
        assertEquals(100L, po.getDeviceId());
        assertEquals(200L, po.getUserId());
        assertEquals("description", po.getDescription());
        assertEquals("http://image.url", po.getImage());
        assertEquals(1, po.getStatus());
        assertEquals(0, po.getIsDelete());
        assertEquals(now, po.getCreateTime());
        assertEquals(now, po.getUpdateTime());
        assertEquals(300L, po.getUserMaintenanceRecordId());
    }
}
