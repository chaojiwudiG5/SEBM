package group5.sebm.notifiation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import group5.sebm.notifiation.entity.TemplatePo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知模板数据访问层
 */
@Mapper
public interface TemplateDao extends BaseMapper<TemplatePo> {
    
    // BaseMapper 已经提供了基本的 CRUD 操作，包括：
    // int insert(TemplatePo entity);
    // int deleteById(Serializable id);
    // int updateById(TemplatePo entity);
    // TemplatePo selectById(Serializable id);
    // List<TemplatePo> selectList(Wrapper<TemplatePo> queryWrapper);
    
    // 如需自定义 SQL，可以在这里添加方法
}
