package group5.sebm.Maintenance.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户报修单创建请求
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    /**
     * 借用记录ID
     */
    @NotNull(message = "borrow record ID cannot be null")
    private Long borrowRecordId;

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