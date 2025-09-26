package group5.sebm.Maintenance.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 技工认领报修单请求
 */
@Data
public class MechanicanClaimDto {

    /**
     * 用户报修单ID
     */
    @NotNull(message = "用户报修单ID不能为空")
    private Long userMaintenanceRecordId;

    /**
     * 维修备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    private String description;
}