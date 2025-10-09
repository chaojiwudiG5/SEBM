package group5.sebm.notifiation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import group5.sebm.notifiation.entity.NotificationRecordPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知记录 Mapper
 */
@Mapper
public interface NotificationRecordMapper extends BaseMapper<NotificationRecordPo> {

}

