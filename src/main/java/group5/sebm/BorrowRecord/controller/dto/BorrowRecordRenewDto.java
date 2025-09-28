package group5.sebm.BorrowRecord.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;

/**
 * 借用记录更新 DTO
 */
@Data
@Deprecated
public class BorrowRecordRenewDto {

  /**
   * 借用记录ID（必填，用于定位要更新的记录）
   */
  @NotNull(message = "借用记录ID不能为空")
  private Long id;

  /**
   * 借阅人ID（必填，用于定位用户）
   */
  @NotNull(message = "借阅人不能为空")
  private Long userId;
  /**
   * 应还时间（用户或管理员可以延长/修改）
   */
  private Date dueTime;
}
