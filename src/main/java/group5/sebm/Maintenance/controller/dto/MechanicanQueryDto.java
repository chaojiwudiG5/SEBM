package group5.sebm.Maintenance.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 技工维修单分页查询
 */
@Data
public class MechanicanQueryDto {

    /**
     * 页码
     */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNumber;

    /**
     * 每页数量
     */
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数必须大于等于1")
    private Integer pageSize;

    /**
     * 设备筛选
     */
    private Long deviceId;

    /**
     * 状态筛选
     */
    private Integer status;
}