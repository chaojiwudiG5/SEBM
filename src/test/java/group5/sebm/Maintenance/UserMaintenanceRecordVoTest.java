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
    @Test
    void testGetterSetterAndAllArgsConstructor() {
        Date now = new Date();

        // 使用有参构造函数
        UserMaintenanceRecordVo vo = new UserMaintenanceRecordVo(
            1L,            // id
            "Device A",    // deviceName
            10L,           // userId
            "description", // description
            "http://image.url", // image
            0,             // status
            now,           // createTime
            now            // updateTime
        );

        // 验证 getter
        assertEquals(1L, vo.getId());
        assertEquals("Device A", vo.getDeviceName());
        assertEquals(10L, vo.getUserId());
        assertEquals("description", vo.getDescription());
        assertEquals("http://image.url", vo.getImage());
        assertEquals(0, vo.getStatus());
        assertEquals(now, vo.getCreateTime());
        assertEquals(now, vo.getUpdateTime());

        // 验证 setter
        vo.setStatus(1);
        assertEquals(1, vo.getStatus());
    }
}
