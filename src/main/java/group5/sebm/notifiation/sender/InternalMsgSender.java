package group5.sebm.notifiation.sender;

import group5.sebm.User.service.UserServiceInterface.UserService;
import group5.sebm.common.dto.UserDto;
import group5.sebm.notifiation.enums.NotificationMethodEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内部消息发送器实现类
 * 实现系统内部消息通知
 */
@Slf4j
@Component
public class InternalMsgSender extends ChannelMsgSender {

    @Autowired
    private UserService userService;

    // 简单的内存存储，实际项目中应该使用数据库或消息队列
    private final Map<String, StringBuilder> userMessages = new ConcurrentHashMap<>();

    @Override
    public NotificationMethodEnum getChannelType() {
        return NotificationMethodEnum.INTERNAL_MSG;
    }

    @Override
    public boolean sendNotification(Long userId, String subject, String content) {
        try {
            log.info("开始发送内部消息通知 - 用户ID: {}, 主题: {}", userId, subject);

            // 验证用户ID
            if (userId == null) {
                log.error("用户ID不能为空");
                return false;
            }

            // 验证用户是否存在
            if (!isUserExists(userId)) {
                log.warn("用户不存在: userId={}", userId);
                return false;
            }

            // 获取用户信息（用于日志记录）
            UserDto userDto = userService.getCurrentUserDtoFromID(userId);
            String username = Objects.nonNull(userDto) ? userDto.getUsername() : "用户" + userId;

            // 格式化消息
            String formattedMessage = formatInternalMessage(subject, content, null);

            // 存储消息到用户消息队列中
            userMessages.computeIfAbsent(userId.toString(), k -> new StringBuilder())
                      .append(formattedMessage)
                      .append("\n");

            // 模拟实时推送（实际项目中可以使用 WebSocket、SSE 等）
            pushToUser(userId.toString(), formattedMessage);

            log.info("内部消息发送成功 - 用户ID: {}, 用户名: {}, 主题: {}", userId, username, subject);
            return true;

        } catch (Exception e) {
            log.error("内部消息发送失败 - 用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 格式化内部消息
     * @param subject 主题
     * @param content 内容
     * @param templateId 模板ID
     * @return 格式化后的消息
     */
    private String formatInternalMessage(String subject, String content, Long templateId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return String.format("[%s] %s: %s %s",
                timestamp,
                subject,
                content,
                templateId != null ? "(模板ID: " + templateId + ")" : "");
    }

    /**
     * 推送消息给用户（模拟实时推送）
     * @param userId 用户ID
     * @param message 消息内容
     */
    private void pushToUser(String userId, String message) {
        // 这里可以集成 WebSocket、Server-Sent Events (SSE) 或消息队列
        log.info("实时推送消息给用户 {} : {}", userId, message);

        // TODO: 实际项目中可以在这里实现：
        // 1. WebSocket 推送
        // 2. SSE 推送
        // 3. 消息队列发布
        // 4. 数据库存储未读消息
    }

    /**
     * 获取用户的所有消息
     * @param userId 用户ID
     * @return 用户消息列表
     */
    public String getUserMessages(String userId) {
        StringBuilder messages = userMessages.get(userId);
        return messages != null ? messages.toString() : "暂无消息";
    }

    /**
     * 清空用户消息
     * @param userId 用户ID
     */
    public void clearUserMessages(String userId) {
        userMessages.remove(userId);
        log.info("已清空用户 {} 的消息记录", userId);
    }

    /**
     * 检查用户是否存在
     * @param userId 用户ID
     * @return 用户是否存在
     */
    private boolean isUserExists(Long userId) {
        try {
            UserDto userDto = userService.getCurrentUserDtoFromID(userId);
            return Objects.nonNull(userDto);
        } catch (Exception e) {
            log.error("检查用户存在性失败 - userId: {}, error: {}", userId, e.getMessage());
        }
        return false;
    }

    /**
     * 获取消息统计
     * @return 消息统计信息
     */
    public Map<String, Object> getMessageStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("totalUsers", userMessages.size());
        stats.put("totalMessages", userMessages.values().stream()
                .mapToInt(sb -> sb.toString().split("\n").length)
                .sum());
        stats.put("timestamp", LocalDateTime.now());
        return stats;
    }
}
