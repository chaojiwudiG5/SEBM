package group5.sebm.BorrowRecord.controller.dto;

import group5.sebm.common.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;

/**
 * 分页查询 BorrowRecord 的 DTO
 */
@Data
public class BorrowRecordQueryDto extends PageDto {

    /**
     * 借用人ID
     */
    @NotNull(message = "User ID cannot be null")
    private Long userId;
}
