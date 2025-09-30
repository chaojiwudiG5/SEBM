package group5.sebm.Reservation.service.services;

import com.baomidou.mybatisplus.extension.service.IService;
import group5.sebm.Reservation.controller.dto.ReservationAddDto;
import group5.sebm.Reservation.controller.dto.ReservationCancelDto;
import group5.sebm.Reservation.controller.dto.ReservationConfirmDto;
import group5.sebm.Reservation.controller.vo.ReservationVo;
import group5.sebm.Reservation.entity.ReservationPo;
import group5.sebm.common.dto.PageDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Luoimo
* @description 针对表【reservation(设备预约表)】的数据库操作Service
* @createDate 2025-09-28 00:18:21
*/
public interface ReservationService extends IService<ReservationPo> {

  ReservationVo reserveDevice(ReservationAddDto reservationAddDto, HttpServletRequest request);

  Boolean cancelReservation(ReservationCancelDto reservationCancelDto, HttpServletRequest request);

  List<ReservationVo> getReservationList(PageDto pageDto, HttpServletRequest request);

  Boolean confirmReservation(ReservationConfirmDto reservationConfirmDto, HttpServletRequest request);
}
