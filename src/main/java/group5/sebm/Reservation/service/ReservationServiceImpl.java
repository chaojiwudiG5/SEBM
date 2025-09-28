package group5.sebm.Reservation.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.Reservation.controller.dto.ReservationAddDto;
import group5.sebm.Reservation.controller.vo.ReservationVo;
import group5.sebm.Reservation.dao.ReservationMapper;
import group5.sebm.Reservation.entity.ReservationPo;
import group5.sebm.Reservation.service.services.ReservationService;
import org.springframework.stereotype.Service;


/**
* @author Luoimo
* @description 针对表【reservation(设备预约表)】的数据库操作Service实现
* @createDate 2025-09-28 00:18:21
*/
@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, ReservationPo>
    implements ReservationService {

  @Override
  public ReservationVo reserveDevice(ReservationAddDto reservationAddDto) {
    return null;
  }
}




