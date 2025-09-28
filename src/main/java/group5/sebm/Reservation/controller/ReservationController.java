package group5.sebm.Reservation.controller;

import group5.sebm.Reservation.controller.dto.ReservationAddDto;
import group5.sebm.Reservation.controller.vo.ReservationVo;
import group5.sebm.Reservation.service.services.ReservationService;
import group5.sebm.common.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public BaseResponse<ReservationVo> reserveDevice(ReservationAddDto reservationAddDto){
        ReservationVo reservationVo = reservationService.reserveDevice(reservationAddDto);
        log.info("Reservation successful: {}", reservationVo);
        return new BaseResponse<>(200, reservationVo);
    }
}