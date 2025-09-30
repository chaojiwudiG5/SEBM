package group5.sebm.BorrowRecord.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BorrowRecordQueryWithStatusDto extends BorrowRecordQueryDto {
  @NotNull(message = "Status cannot be null")
  private Integer status;
}
