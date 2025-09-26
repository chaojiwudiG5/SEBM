package group5.sebm.Maintenance.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.Maintenance.dao.UserMaintenanceRecordMapper;
import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
import group5.sebm.Maintenance.service.services.UserMaintenanceRecordService;
import org.springframework.stereotype.Service;

/**
* @author Luoimo
* @description 针对表【userMaintenanceRecord(设备维修报单表)】的数据库操作Service实现
* @createDate 2025-09-26 13:41:45
*/
@Service
public class UserMaintenanceRecordServiceImpl extends ServiceImpl<UserMaintenanceRecordMapper, UserMaintenanceRecordPo>
    implements UserMaintenanceRecordService {

}




