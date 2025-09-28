package group5.sebm.Reservation.controller.dto;

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
    private Long deviceId;

    /**
     * 预约开始时间
     */
    private Date reserveStart;

    /**
     * 预约结束时间
     */
    private Date reserveEnd;

    /**
     * 备注
     */
    private String remarks;
}
