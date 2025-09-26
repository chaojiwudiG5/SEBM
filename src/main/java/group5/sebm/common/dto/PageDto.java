package group5.sebm.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {

  /**
   * 页码，从1开始
   */
  @NotNull(message = "Page number cannot be null")
  @Min(value = 1, message = "Page number must be greater than or equal to 1")
  private Integer pageNumber;

  /**
   * 每页条数
   */
  @NotNull(message = "Page size cannot be null")
  @Min(value = 1, message = "Page size must be greater than or equal to 1")
  private Integer pageSize;
}
