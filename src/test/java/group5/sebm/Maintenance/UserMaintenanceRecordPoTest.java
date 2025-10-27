package group5.sebm.Maintenance;

import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserMaintenanceRecordPoTest {

    @Test
    void testGetterAndSetter() {
        UserMaintenanceRecordPo po = new UserMaintenanceRecordPo();

        po.setId(1L);
        po.setDeviceId(100L);
        po.setUserId(10L);
        po.setDescription("description");
        po.setImage("http://image.url");
        po.setStatus(0);
        po.setIsDelete(0);
        Date now = new Date();
        po.setCreateTime(now);
        po.setUpdateTime(now);

        assertEquals(1L, po.getId());
        assertEquals(100L, po.getDeviceId());
        assertEquals(10L, po.getUserId());
        assertEquals("description", po.getDescription());
        assertEquals("http://image.url", po.getImage());
        assertEquals(0, po.getStatus());
        assertEquals(0, po.getIsDelete());
        assertEquals(now, po.getCreateTime());
        assertEquals(now, po.getUpdateTime());
    }
}
