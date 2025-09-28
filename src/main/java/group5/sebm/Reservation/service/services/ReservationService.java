package group5.sebm.Reservation.service.services;

import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.Reservation.controller.dto.ReservationAddDto;
import group5.sebm.Reservation.controller.vo.ReservationVo;
import group5.sebm.Reservation.entity.ReservationPo;

/**
* @author Luoimo
* @description 针对表【reservation(设备预约表)】的数据库操作Service
* @createDate 2025-09-28 00:18:21
*/
public interface ReservationService extends IService<ReservationPo> {

  ReservationVo reserveDevice(ReservationAddDto reservationAddDto);
}
