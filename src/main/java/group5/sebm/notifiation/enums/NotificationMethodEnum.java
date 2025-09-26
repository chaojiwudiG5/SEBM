package group5.sebm.notifiation.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知方式枚举
 */
@Getter
@AllArgsConstructor
public enum NotificationMethodEnum {

    EMAIL(1, "邮件"),
    // todo 短信或者whatapp, 后期视情况而定
    SMS(2, "短信"),
    INTERNAL_MSG(3, "站内信");

    private final Integer code;
    private final String description;

    /**
     * 根据code获取枚举
     */
    public static NotificationNodeEnum parseNode(Integer code) {
        if (code == null) {
            return null;
        }
        for (NotificationNodeEnum nodeEnum : NotificationNodeEnum.values()) {
            if (nodeEnum.getCode().equals(code)) {
                return nodeEnum;
            }
        }
        return null;
    }

    /**
     * 验证code是否有效
     */
    public static boolean isValidCode(Integer code) {
        return parseNode(code) != null;
    }

}
