package group5.sebm.notifiation.enums;

import lombok.Getter;

/**
 * 通知记录状态枚举
 */
@Getter
public enum NotificationRecordStatusEnum {
    
    /**
     * 待发送
     */
    PENDING(0, "待发送"),
    
    /**
     * 发送成功
     */
    SUCCESS(1, "发送成功"),
    
    /**
     * 发送失败
     */
    FAILED(2, "发送失败");
    
    private final Integer code;
    private final String desc;
    
    NotificationRecordStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

