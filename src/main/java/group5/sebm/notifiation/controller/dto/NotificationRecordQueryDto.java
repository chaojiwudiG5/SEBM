package group5.sebm.notifiation.controller.dto;

import group5.sebm.common.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 通知记录查询 DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRecordQueryDto extends PageDto {
    
    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 发送状态（可选）0-待发送, 1-发送成功, 2-发送失败
     */
    private Integer status;
    
    /**
     * 标题关键词（可选）
     */
    private String titleKeyword;
}

