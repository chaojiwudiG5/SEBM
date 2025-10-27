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
    }
    @Test
    void testGetterSetterAndAllArgsConstructor() {
        Date now = new Date();

        // 使用有参构造函数
        MechanicanMaintenanceRecordPo po = new MechanicanMaintenanceRecordPo(
            1L,          // id
            100L,        // deviceId
            200L,        // userId
            "description", // description
            "http://image.url", // image
            1,           // status
            0,           // isDelete
            now,         // createTime
            now,         // updateTime
            300L         // userMaintenanceRecordId
        );

        // 验证 getter
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

        // setter 测试
        po.setDescription("new description");
        assertEquals("new description", po.getDescription());
    }
}
