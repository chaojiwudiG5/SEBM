package group5.sebm.notifiation.sender;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * SendGrid邮件发送服务
 * 用于云部署环境，绕过SMTP端口限制
 */
@Slf4j
@Service
public class SendGridEmailService {

    @Value("${sendgrid.api.key:P9VUTFHBERA884FSADBLKQ4J}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email:sebm.notifications@gmail.com}")
    private String fromEmail;

    @Value("${sendgrid.from.name:SEBM Notification System}")
    private String fromName;

    /**
     * 使用SendGrid API发送邮件
     * 
     * @param toEmail   收件人邮箱
     * @param subject   邮件主题
     * @param content   邮件内容
     * @return 是否发送成功
     */
    public boolean sendEmail(String toEmail, String subject, String content) {
        // 检查API Key是否配置
        if (sendGridApiKey == null || sendGridApiKey.trim().isEmpty()) {
            log.warn("SendGrid API Key未配置，跳过SendGrid发送");
            return false;
        }

        try {
            Email from = new Email(fromEmail, fromName);
            Email to = new Email(toEmail);
            Content emailContent = new Content("text/plain", content);
            Mail mail = new Mail(from, subject, to, emailContent);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("SendGrid邮件发送成功 - 收件人: {}, 主题: {}, 状态码: {}", 
                        toEmail, subject, response.getStatusCode());
                return true;
            } else {
                log.error("SendGrid邮件发送失败 - 收件人: {}, 状态码: {}, 响应: {}", 
                        toEmail, response.getStatusCode(), response.getBody());
                return false;
            }
            
        } catch (IOException e) {
            log.error("SendGrid邮件发送异常 - 收件人: {}, 错误: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查SendGrid是否已配置
     * 
     * @return 是否已配置
     */
    public boolean isConfigured() {
        return sendGridApiKey != null && !sendGridApiKey.trim().isEmpty();
    }
}

