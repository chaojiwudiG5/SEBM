package group5.sebm.Maintenance.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.Maintenance.dao.MechanicanMaintenanceRecordMapper;
import group5.sebm.Maintenance.entity.MechanicanMaintenanceRecordPo;
import group5.sebm.Maintenance.service.services.MechanicanMaintenanceRecordService;
import org.springframework.stereotype.Service;

/**
* @author Luoimo
* @description 针对表【mechanicanMaintenanceRecord(技工设备维修报单表)】的数据库操作Service实现
* @createDate 2025-09-26 13:41:31
*/
@Service
public class MechanicanMaintenanceRecordServiceImpl extends ServiceImpl<MechanicanMaintenanceRecordMapper, MechanicanMaintenanceRecordPo>
    implements MechanicanMaintenanceRecordService {

}




