package group5.sebm.BorrowRecord.controller.dto;

import java.util.Date;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.Data;

/**
 * 前端创建新的借用记录 DTO
 */
@Data
public class BorrowRecordAddDto {

    /**
     * 借用设备ID
     */
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    /**
     * 借出时间
     */
    @NotNull(message = "借出时间不能为空")
    private Date borrowTime;

    /**
     * 应还时间，必须在借出时间之后
     */
    @NotNull(message = "应还时间不能为空")
    @Future(message = "应还时间必须是未来时间")
    private Date dueTime;

    /**
     * 备注，可选
     */
    private String remarks;
}
