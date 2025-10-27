package group5.sebm.Maintenance;

import group5.sebm.Maintenance.entity.MechanicanMaintenanceRecordPo;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MechanicanMaintenanceRecordPoTest {

    @Test
    void testGetterSetterAndLombokMethods() {
        MechanicanMaintenanceRecordPo po = new MechanicanMaintenanceRecordPo();

        Date now = new Date();

        po.setId(1L);
        po.setDeviceId(100L);
        po.setUserId(200L);
        po.setDescription("description");
        po.setImage("http://image.url");
        po.setStatus(1);
        po.setIsDelete(0);
        po.setCreateTime(now);
        po.setUpdateTime(now);
        po.setUserMaintenanceRecordId(300L);

        // getter
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

        // toString()
        assertTrue(po.toString().contains("description"));

        // equals() & hashCode()
        MechanicanMaintenanceRecordPo po2 = new MechanicanMaintenanceRecordPo();
        po2.setId(1L);
        po2.setDeviceId(100L);
        po2.setUserId(200L);
        po2.setDescription("description");
        po2.setImage("http://image.url");
        po2.setStatus(1);
        po2.setIsDelete(0);
        po2.setCreateTime(now);
        po2.setUpdateTime(now);
        po2.setUserMaintenanceRecordId(300L);

        assertEquals(po, po2);           // equals
        assertEquals(po.hashCode(), po2.hashCode()); // hashCode
    }
}
