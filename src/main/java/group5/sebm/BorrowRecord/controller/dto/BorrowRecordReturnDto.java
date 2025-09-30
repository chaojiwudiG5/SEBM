package group5.sebm.BorrowRecord.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;

@Data
public class BorrowRecordReturnDto {
  /**
   * 借用记录ID（必填，用于定位要更新的记录）
   */
  @NotNull(message = "借用记录ID不能为空")
  private Long id;
  /**
   * 实际归还时间（归还时填写）
   */
  private Date returnTime;
  /**
   * 备注（例如归还时填写损坏情况等）
   */
  private String remarks;
}
