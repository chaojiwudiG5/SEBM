package group5.sebm.BorrowRecord.controller.dto;

import lombok.Data;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 预约设备请求 DTO
 */
@Data
public class DeviceReservationDto {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    @NotNull(message = "预约开始时间不能为空")
    @Future(message = "预约开始时间必须在未来")
    private LocalDateTime reserveStart;

    @NotNull(message = "预约结束时间不能为空")
    @Future(message = "预约结束时间必须在未来")
    private LocalDateTime reserveEnd;

    @Size(max = 200, message = "备注不能超过200字")
    private String remark;

}
