package group5.sebm.Reservation.controller.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 设备预约请求参数
 */
@Data
public class ReservationAddDto {
    /**
     * 预约设备ID
     */
    @NotNull(message = "Device ID cannot be null")
    private Long deviceId;

    /**
     * 预约开始时间
     */
    @NotNull(message = "Reservation start time cannot be null")
    @FutureOrPresent(message = "Reservation start time cannot be in the past")
    private Date reserveStart;

    /**
     * 预约结束时间
     */
    @NotNull(message = "Reservation end time cannot be null")
    @Future(message = "Reservation end time cannot be in the past")
    private Date reserveEnd;

    /**
     * 备注
     */
    private String remarks;
}
