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
    @Test
    void testGetterSetterAndAllArgsConstructor() {
        Date now = new Date();

        // 使用有参构造函数
        MechanicanMaintenanceRecordVo vo = new MechanicanMaintenanceRecordVo(
            1L,          // id
            100L,        // deviceId
            200L,        // userId
            "description", // description
            "http://image.url", // image
            1,           // status
            now,         // createTime
            now,         // updateTime
            300L         // userMaintenanceRecordId
        );

        // 验证 getter
        assertEquals(1L, vo.getId());
        assertEquals(100L, vo.getDeviceId());
        assertEquals(200L, vo.getUserId());
        assertEquals("description", vo.getDescription());
        assertEquals("http://image.url", vo.getImage());
        assertEquals(1, vo.getStatus());
        assertEquals(now, vo.getCreateTime());
        assertEquals(now, vo.getUpdateTime());
        assertEquals(300L, vo.getUserMaintenanceRecordId());

        // 验证 setter
        vo.setDescription("new description");
        assertEquals("new description", vo.getDescription());
    }
}
