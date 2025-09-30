package group5.sebm.notifiation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 发送通知请求DTO - 为其他服务提供的统一接口
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendNotificationDto {
    
    /**
     * 通知节点枚举值
     * 使用 NotificationNodeEnum
     */
    private Integer notificationNode;

    /**
     * 通知角色枚举值
     * 使用 NotificationRoleEnum
     */
    private Integer notificationRole;
    
    /**
     * 接收者Id
     */
    @NotBlank(message = "接收者不能为空")
    private Long userId;

    /**
     * 占位符信息
     * 例如: {"orderNo": "12345", "equipmentName": "笔记本电脑", "remainingTime": "5分钟"}
     */
    private Map<String, Object> templateVars;

    /**
     * 延迟时间
     */
    private Long delaySeconds;

    /**
     * 验证请求参数
     * @return 是否有效
     */
    public boolean isValid() {
        // 必须提供事件代码或通知节点代码之一
        if (notificationNode == null || userId == null || notificationRole == null) {
            return false;
        }
        return true;
    }

}
