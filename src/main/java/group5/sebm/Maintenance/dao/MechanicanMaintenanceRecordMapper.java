package group5.sebm.Maintenance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import group5.sebm.Maintenance.entity.MechanicanMaintenanceRecordPo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Luoimo
* @description 针对表【mechanicanMaintenanceRecord(技工设备维修报单表)】的数据库操作Mapper
* @createDate 2025-09-26 13:41:31
* @Entity generator.domain.MechanicanMaintenanceRecord
*/
@Mapper
public interface MechanicanMaintenanceRecordMapper extends BaseMapper<MechanicanMaintenanceRecordPo> {

}




