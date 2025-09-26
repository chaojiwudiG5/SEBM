package group5.sebm.Maintenance.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 技工更新维修单状态
 */
@Data
public class MechanicanUpdateDto {

    /**
     * 技工维修单ID
     */
    @NotNull(message = "维修单ID不能为空")
    private Long id;

    /**
     * 新状态：0-待处理，1-处理中，2-已修复，3-无法修复
     */
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "非法的状态值")
    @Max(value = 3, message = "非法的状态值")
    private Integer status;

    /**
     * 维修描述
     */
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;

    /**
     * 完成图片
     */
    @Size(max = 1024, message = "图片地址过长")
    private String image;

    /**
     * 关联的用户报修单ID，可用于同步状态
     */
    private Long userMaintenanceRecordId;
}