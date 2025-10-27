package group5.sebm.notifiation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import group5.sebm.notifiation.entity.NotificationTaskPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知任务 Mapper
 */
@Mapper
public interface NotificationTaskMapper extends BaseMapper<NotificationTaskPo> {

    /**
     * 根据角色查询通知任务
     * @param notificationRole 通知角色
     * @return 任务列表
     */
    List<NotificationTaskPo> selectByRole(@Param("notificationRole") Integer notificationRole);

}

