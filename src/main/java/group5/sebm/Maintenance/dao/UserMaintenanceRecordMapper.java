package group5.sebm.Maintenance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import group5.sebm.Maintenance.entity.UserMaintenanceRecordPo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Luoimo
* @description 针对表【userMaintenanceRecord(设备维修报单表)】的数据库操作Mapper
* @createDate 2025-09-26 13:41:45
* @Entity generator.domain.UserMaintenanceRecord
*/
@Mapper
public interface UserMaintenanceRecordMapper extends BaseMapper<UserMaintenanceRecordPo> {

}




