package group5.sebm.Order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import group5.sebm.Order.entity.OrderPo;
import group5.sebm.User.entity.UserPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Deshperaydon
 * @date 2025/9/26
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderPo> {
}




