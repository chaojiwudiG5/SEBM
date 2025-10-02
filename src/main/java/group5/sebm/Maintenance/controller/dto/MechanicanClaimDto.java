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
    @NotNull(message = "userMaintenanceRecordId cannot be null")
    private Long userMaintenanceRecordId;

    /**
     * 维修备注
     */
    @Size(max = 500, message = "description cannot exceed 500 characters")
    private String description;
}