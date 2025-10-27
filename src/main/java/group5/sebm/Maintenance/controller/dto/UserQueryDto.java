package group5.sebm.Maintenance.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户报修单查询请求
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryDto {

    /**
     * 页码
     */
    @NotNull(message = "pageNumber cannot be null")
    @Min(value = 1, message = "pageNumber must be greater than or equal to 1")
    private Integer pageNumber;

    /**
     * 每页数量
     */
    @NotNull(message = "pageSize cannot be null")
    @Min(value = 1, message = "pageSize must be greater than or equal to 1")
    private Integer pageSize;
    /**
     * 状态筛选
     */
    private Integer status;
}