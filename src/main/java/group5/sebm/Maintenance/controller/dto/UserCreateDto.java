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
    @NotNull(message = "deviceId cannot be null")
    private Long deviceId;

    /**
     * 故障描述
     */
    @NotBlank(message = "description cannot be blank")
    @Size(max = 500, message = "description cannot exceed 500 characters")
    private String description;

    /**
     * 故障图片
     */
    //image URL cannot be blank
    @NotBlank(message = "image URL cannot be blank")
    @Size(max = 1024, message = "image URL is too long")
    private String image;
}