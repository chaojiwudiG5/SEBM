package group5.sebm.Reservation.controller;

import group5.sebm.Reservation.controller.dto.ReservationAddDto;
import group5.sebm.Reservation.controller.dto.ReservationCancelDto;
import group5.sebm.Reservation.controller.dto.ReservationConfirmDto;
import group5.sebm.Reservation.controller.vo.ReservationVo;
import group5.sebm.Reservation.service.services.ReservationService;
import group5.sebm.common.BaseResponse;
import group5.sebm.common.ResultUtils;
import group5.sebm.common.dto.PageDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Reservation")
@RequestMapping("/reservation")
@AllArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @PostMapping("/reserveDevice")
  public BaseResponse<ReservationVo> reserveDevice(ReservationAddDto reservationAddDto,
      HttpServletRequest request) {
    ReservationVo reservationVo = reservationService.reserveDevice(reservationAddDto, request);
    log.info("Reservation successful: {}", reservationVo);
    return ResultUtils.success(reservationVo);
  }

  @PostMapping("/cancelReservation")
  public BaseResponse<Boolean> cancelReservation(ReservationCancelDto reservationCancelDto,
      HttpServletRequest request) {
        Boolean isCanceled = reservationService.cancelReservation(reservationCancelDto, request);
        log.info("Reservation canceled: {}", isCanceled);
        return ResultUtils.success(isCanceled);
  }

  @PostMapping("/getReservationList")
  public BaseResponse<List<ReservationVo>> getReservationList(PageDto pageDto, HttpServletRequest request) {
    List<ReservationVo> reservationVoList = reservationService.getReservationList(pageDto,request);
    log.info("Reservation list: {}", reservationVoList);
    return ResultUtils.success(reservationVoList);
  }

  @PostMapping("/confirmReservation")
  public BaseResponse<Boolean> confirmReservation(ReservationConfirmDto reservationConfirmDto,
      HttpServletRequest request) {
        Boolean isConfirmed = reservationService.confirmReservation(reservationConfirmDto, request);
        log.info("Reservation confirmed: {}", isConfirmed);
        return ResultUtils.success(isConfirmed);
      }

}