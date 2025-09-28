package group5.sebm.User.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import group5.sebm.User.entity.BorrowerInfoPo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Luoimo
* @description 针对表【borrower_info(借用者)】的数据库操作Mapper
* @createDate 2025-09-28 15:48:22
* @Entity generator.domain.BorrowerInfo
*/
@Mapper
public interface BorrowerInfoMapper extends BaseMapper<BorrowerInfoPo> {

}




