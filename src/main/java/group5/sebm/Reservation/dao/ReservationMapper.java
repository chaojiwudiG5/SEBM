package group5.sebm.Reservation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import group5.sebm.Reservation.entity.ReservationPo;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Luoimo
* @description 针对表【reservation(设备预约表)】的数据库操作Mapper
* @createDate 2025-09-28 00:18:21
* @Entity generator.domain.Reservation
*/
@Mapper
public interface ReservationMapper extends BaseMapper<ReservationPo> {

}




