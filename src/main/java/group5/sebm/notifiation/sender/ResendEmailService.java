package group5.sebm.notifiation.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Resend邮件发送服务
 * 用于云部署环境，绕过SMTP端口限制
 * Resend文档: https://resend.com/docs/send-with-java
 */
@Slf4j
@Service
public class ResendEmailService {

    @Value("${resend.api.key:re_7tcg92BP_7HVbMYC4aAX5KgYUiTan3NDF}")
    private String resendApiKey;

    @Value("${resend.from.email:onboarding@resend.dev}")
    private String fromEmail;

    @Value("${resend.from.name:SEBM Notification System}")
    private String fromName;

    @Value("${resend.reply.to:}")
    private String replyTo;

    private static final String RESEND_API_URL = "https://api.resend.com/emails";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ResendEmailService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 使用Resend API发送邮件
     * 
     * @param toEmail   收件人邮箱
     * @param subject   邮件主题
     * @param content   邮件内容
     * @return 是否发送成功
     */
    public boolean sendEmail(String toEmail, String subject, String content) {
        // 检查API Key是否配置
        if (resendApiKey == null || resendApiKey.trim().isEmpty()) {
            log.warn("Resend API Key未配置，跳过Resend发送");
            return false;
        }

        try {
            // 构建邮件数据
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("from", fromName + " <" + fromEmail + ">");
            emailData.put("to", new String[]{toEmail});
            emailData.put("subject", subject);
            emailData.put("text", content);
            
            // 如果配置了reply-to，添加回复地址
            if (replyTo != null && !replyTo.trim().isEmpty()) {
                emailData.put("reply_to", replyTo);
            }

            String requestBody = objectMapper.writeValueAsString(emailData);

            // 构建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RESEND_API_URL))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 发送请求
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Resend邮件发送成功 - 收件人: {}, 主题: {}, 状态码: {}", 
                        toEmail, subject, response.statusCode());
                return true;
            } else {
                log.error("Resend邮件发送失败 - 收件人: {}, 状态码: {}, 响应: {}", 
                        toEmail, response.statusCode(), response.body());
                return false;
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 保留中断状态
            log.error("线程被中断: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Resend邮件发送异常 - 收件人: {}, 错误: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查Resend是否已配置
     * 
     * @return 是否已配置
     */
    public boolean isConfigured() {
        return resendApiKey != null && !resendApiKey.trim().isEmpty();
    }
}

