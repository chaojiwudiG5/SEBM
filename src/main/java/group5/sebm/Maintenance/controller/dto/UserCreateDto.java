package group5.sebm.Maintenance.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户报修单创建请求
 */
@Data
public class UserCreateDto {

    /**
     * 设备ID
     */
    @NotNull(message = "设备ID不能为空")
    private Long deviceId;

    /**
     * 故障描述
     */
    @NotBlank(message = "故障描述不能为空")
    @Size(max = 500, message = "故障描述不能超过500个字符")
    private String description;

    /**
     * 故障图片
     */
    @Size(max = 1024, message = "图片地址过长")
    private String image;
}