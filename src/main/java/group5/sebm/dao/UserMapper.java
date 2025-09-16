package group5.sebm.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import group5.sebm.entity.UserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<UserPo> {

}
