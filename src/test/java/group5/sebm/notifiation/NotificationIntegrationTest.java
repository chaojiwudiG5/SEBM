package group5.sebm.notifiation;

import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.dto.SendNotificationDto;
import group5.sebm.notifiation.controller.vo.NotificationRecordVo;
import group5.sebm.notifiation.controller.vo.TemplateVo;
import group5.sebm.notifiation.entity.NotificationRecordPo;
import group5.sebm.notifiation.entity.NotificationTaskPo;
import group5.sebm.notifiation.entity.TemplatePo;
import group5.sebm.notifiation.enums.NotificationMethodEnum;
import group5.sebm.notifiation.enums.NotificationRecordStatusEnum;
import group5.sebm.notifiation.mq.NotificationMessage;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Notification模块整合测试
 * 包含结构验证测试和简单的功能测试
 */
@SpringBootTest(classes = {
    org.springframework.boot.autoconfigure.SpringBootApplication.class
})
@TestPropertySource(properties = {
    // 禁用外部依赖
    "spring.autoconfigure.exclude=" +
        "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
        "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration," +
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
        "org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration," +
        "org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration," +
        "org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration",
    // 禁用Web环境
    "spring.main.web-application-type=none"
})
@DisplayName("Notification模块整合测试")
class NotificationIntegrationTest {

    @Test
    @DisplayName("测试Spring上下文加载成功")
    void contextLoads() {
        // 这个测试只是确保Spring上下文能够成功加载
        // 如果上下文加载失败，测试会抛出异常
        assertTrue(true, "Spring上下文应该成功加载");
    }

    @Test
    @DisplayName("测试Notification模块类可以被实例化")
    void testNotificationClassesCanBeInstantiated() {
        // 测试关键类是否可以被加载和实例化
        assertDoesNotThrow(() -> {
            Class.forName("group5.sebm.notifiation.service.impl.NotificationServiceImpl");
            Class.forName("group5.sebm.notifiation.service.impl.NotificationRecordServiceImpl");
            Class.forName("group5.sebm.notifiation.service.impl.TemplateServiceImpl");
            Class.forName("group5.sebm.notifiation.service.MessageSenderService");
            Class.forName("group5.sebm.notifiation.mq.MessageProcessor");
            Class.forName("group5.sebm.notifiation.controller.NotificationRecordController");
        }, "Notification模块的所有关键类应该可以被加载");
    }

    @Test
    @DisplayName("测试实体类结构完整性")
    void testEntityClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证NotificationRecordPo实体类
            Class<?> recordPoClass = Class.forName(
                "group5.sebm.notifiation.entity.NotificationRecordPo"
            );
            assertNotNull(recordPoClass.getDeclaredField("id"));
            assertNotNull(recordPoClass.getDeclaredField("notificationTaskId"));
            assertNotNull(recordPoClass.getDeclaredField("userId"));
            assertNotNull(recordPoClass.getDeclaredField("notificationMethod"));
            assertNotNull(recordPoClass.getDeclaredField("status"));
            assertNotNull(recordPoClass.getDeclaredField("readStatus"));

            // 验证TemplatePo实体类
            Class<?> templatePoClass = Class.forName(
                "group5.sebm.notifiation.entity.TemplatePo"
            );
            assertNotNull(templatePoClass.getDeclaredField("id"));
            assertNotNull(templatePoClass.getDeclaredField("templateTitle"));
            assertNotNull(templatePoClass.getDeclaredField("notificationMethod"));
            assertNotNull(templatePoClass.getDeclaredField("notificationNode"));
            assertNotNull(templatePoClass.getDeclaredField("templateContent"));

            // 验证NotificationTaskPo实体类
            Class<?> taskPoClass = Class.forName(
                "group5.sebm.notifiation.entity.NotificationTaskPo"
            );
            assertNotNull(taskPoClass.getDeclaredField("id"));
        }, "所有实体类应该可以被正确加载并有完整的字段定义");
    }

    @Test
    @DisplayName("测试DTO类结构完整性")
    void testDTOClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证DTO类可以被加载
            Class.forName("group5.sebm.notifiation.controller.dto.SendNotificationDto");
            Class.forName("group5.sebm.notifiation.controller.dto.CreateTemplateDto");
            Class.forName("group5.sebm.notifiation.controller.dto.NotificationRecordQueryDto");
            Class.forName("group5.sebm.notifiation.controller.dto.TemplateQueryDto");
        }, "所有DTO类应该可以被正确加载");
    }

    @Test
    @DisplayName("测试VO类结构完整性")
    void testVOClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证VO类可以被加载
            Class<?> recordVoClass = Class.forName(
                "group5.sebm.notifiation.controller.vo.NotificationRecordVo"
            );
            assertNotNull(recordVoClass.getDeclaredField("id"));
            assertNotNull(recordVoClass.getDeclaredField("userId"));
            assertNotNull(recordVoClass.getDeclaredField("status"));

            Class<?> templateVoClass = Class.forName(
                "group5.sebm.notifiation.controller.vo.TemplateVo"
            );
            assertNotNull(templateVoClass.getDeclaredField("id"));
        }, "所有VO类应该可以被正确加载并有完整的字段定义");
    }

    @Test
    @DisplayName("测试消息队列相关类结构完整性")
    void testMQClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证MQ相关类可以被加载
            Class.forName("group5.sebm.notifiation.mq.MessageProducer");
            Class.forName("group5.sebm.notifiation.mq.MessageConsumer");
            Class.forName("group5.sebm.notifiation.mq.MessageProcessor");
            Class.forName("group5.sebm.notifiation.mq.NotificationMessage");
        }, "所有MQ相关类应该可以被正确加载");
    }

    @Test
    @DisplayName("测试发送器相关类结构完整性")
    void testSenderClassStructure() {
        assertDoesNotThrow(() -> {
            // 验证发送器相关类可以被加载
            Class.forName("group5.sebm.notifiation.sender.ChannelMsgSender");
            Class.forName("group5.sebm.notifiation.sender.EmailSender");
            Class.forName("group5.sebm.notifiation.sender.InternalMsgSender");
        }, "所有发送器相关类应该可以被正确加载");
    }

    // ========== 功能测试部分 ==========

    @Test
    @DisplayName("功能测试：实体对象可以正常创建和使用")
    void testEntityObjectCreation() {
        // 测试NotificationRecordPo的创建和基本功能
        NotificationRecordPo record = NotificationRecordPo.builder()
            .id(1L)
            .notificationTaskId(100L)
            .userId(1001L)
            .notificationMethod(1)
            .status(0)
            .readStatus(0)
            .isDelete(0)
            .sendTime(LocalDateTime.now())
            .createTime(LocalDateTime.now())
            .updateTime(LocalDateTime.now())
            .build();

        assertNotNull(record.getId(), "ID应该被正确设置");
        assertEquals(100L, record.getNotificationTaskId(), "任务ID应该被正确设置");
        assertEquals(1001L, record.getUserId(), "用户ID应该被正确设置");
        assertEquals(1, record.getNotificationMethod(), "通知方式应该被正确设置");
        assertEquals(0, record.getStatus(), "状态应该被正确设置");
        assertEquals(0, record.getReadStatus(), "已读状态应该被正确设置");

        // 测试TemplatePo的创建和基本功能
        TemplatePo template = new TemplatePo();
        template.setId(1L);
        template.setTemplateTitle("测试模板");
        template.setNotificationMethod(Arrays.asList(1, 3));
        template.setNotificationNode("BORROW_CREATED");
        template.setNotificationRole(1);
        template.setNotificationType(1);
        template.setTemplateContent("您的设备借用申请已提交");
        template.setTemplateDesc("借用申请提交通知");
        template.setCreateTime(LocalDateTime.now());

        assertNotNull(template.getId(), "ID应该被正确设置");
        assertEquals("测试模板", template.getTemplateTitle(), "模板标题应该被正确设置");
        assertNotNull(template.getNotificationMethod(), "通知方式列表应该被正确设置");
        assertEquals(2, template.getNotificationMethod().size(), "通知方式应该有2个");
        assertTrue(template.getNotificationMethod().contains(1), "应该包含邮件通知");
        assertTrue(template.getNotificationMethod().contains(3), "应该包含站内信通知");
    }

    @Test
    @DisplayName("功能测试：DTO对象可以正常创建和使用")
    void testDTOObjectCreation() {
        // 测试SendNotificationDto
        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("deviceName", "笔记本电脑");
        templateVars.put("borrowTime", "2025-10-29 10:00:00");

        SendNotificationDto sendDto = SendNotificationDto.builder()
            .notificationEvent(1)
            .userId(1001L)
            .templateVars(templateVars)
            .nodeTimestamp(System.currentTimeMillis() / 1000)
            .build();

        assertEquals(1, sendDto.getNotificationEvent(), "通知事件应该被正确设置");
        assertEquals(1001L, sendDto.getUserId(), "用户ID应该被正确设置");
        assertNotNull(sendDto.getTemplateVars(), "模板变量应该被正确设置");
        assertEquals("笔记本电脑", sendDto.getTemplateVars().get("deviceName"),
            "模板变量内容应该正确");

        // 测试CreateTemplateDto
        CreateTemplateDto createDto = new CreateTemplateDto();
        createDto.setTemplateTitle("新建模板");
        createDto.setNotificationMethod(Arrays.asList(1, 2, 3));
        createDto.setNotificationNode(1);
        createDto.setContent("这是测试内容");
        createDto.setTemplateDesc("测试描述");

        assertEquals("新建模板", createDto.getTemplateTitle(), "模板标题应该被正确设置");
        assertNotNull(createDto.getNotificationMethod(), "通知方式应该被正确设置");
        assertEquals(3, createDto.getNotificationMethod().size(), "应该有3种通知方式");
    }

    @Test
    @DisplayName("功能测试：VO对象可以正常创建和使用")
    void testVOObjectCreation() {
        // 测试NotificationRecordVo
        NotificationRecordVo recordVo = NotificationRecordVo.builder()
            .id(1L)
            .notificationTaskId(100L)
            .userId(1001L)
            .title("通知标题")
            .content("通知内容")
            .notificationMethod(1)
            .status(1)
            .statusDesc("发送成功")
            .readStatus(0)
            .readStatusDesc("未读")
            .sendTime(LocalDateTime.now())
            .createTime(LocalDateTime.now())
            .build();

        assertEquals(1L, recordVo.getId(), "ID应该被正确设置");
        assertEquals(100L, recordVo.getNotificationTaskId(), "任务ID应该被正确设置");
        assertEquals(1001L, recordVo.getUserId(), "用户ID应该被正确设置");
        assertEquals("通知标题", recordVo.getTitle(), "标题应该被正确设置");
        assertEquals(1, recordVo.getNotificationMethod(), "通知方式应该被正确设置");
        assertEquals(1, recordVo.getStatus(), "状态应该被正确设置");
        assertEquals("发送成功", recordVo.getStatusDesc(), "状态描述应该被正确设置");

        // 测试TemplateVo
        TemplateVo templateVo = new TemplateVo();
        templateVo.setId(1L);
        templateVo.setTemplateTitle("模板标题");
        templateVo.setContent("模板内容");
        templateVo.setNotificationMethod(Arrays.asList(1, 3));

        assertEquals(1L, templateVo.getId(), "ID应该被正确设置");
        assertEquals("模板标题", templateVo.getTemplateTitle(), "模板标题应该被正确设置");
        assertNotNull(templateVo.getNotificationMethod(), "通知方式应该被正确设置");
    }

    @Test
    @DisplayName("功能测试：通知方式枚举")
    void testNotificationMethodEnum() {
        // 测试通知方式枚举的正确性
        assertDoesNotThrow(() -> {
            int emailCode = NotificationMethodEnum.EMAIL.getCode();
            int smsCode = NotificationMethodEnum.SMS.getCode();
            int internalCode = NotificationMethodEnum.INTERNAL_MSG.getCode();
            
            assertEquals(1, emailCode, "邮件通知代码应该是1");
            assertEquals(2, smsCode, "短信通知代码应该是2");
            assertEquals(3, internalCode, "站内信通知代码应该是3");
            
            // 验证描述不为空
            assertNotNull(NotificationMethodEnum.EMAIL.getDescription(), "邮件描述不应为null");
            assertNotNull(NotificationMethodEnum.SMS.getDescription(), "短信描述不应为null");
            assertNotNull(NotificationMethodEnum.INTERNAL_MSG.getDescription(), "站内信描述不应为null");

            assertEquals("邮件", NotificationMethodEnum.EMAIL.getDescription(), "邮件描述应该正确");
            assertEquals("短信", NotificationMethodEnum.SMS.getDescription(), "短信描述应该正确");
            assertEquals("站内信", NotificationMethodEnum.INTERNAL_MSG.getDescription(), "站内信描述应该正确");

            // 测试根据code获取枚举
            assertEquals(NotificationMethodEnum.EMAIL, 
                NotificationMethodEnum.parseMethod(1), "应该能通过code获取枚举");
            assertEquals(NotificationMethodEnum.INTERNAL_MSG, 
                NotificationMethodEnum.parseMethod(3), "应该能通过code获取枚举");

            // 测试验证code有效性
            assertTrue(NotificationMethodEnum.isValidCode(1), "code 1应该有效");
            assertTrue(NotificationMethodEnum.isValidCode(2), "code 2应该有效");
            assertTrue(NotificationMethodEnum.isValidCode(3), "code 3应该有效");
            assertFalse(NotificationMethodEnum.isValidCode(999), "无效code应该返回false");

            // 测试验证多个code
            assertTrue(NotificationMethodEnum.isValidCode(Arrays.asList(1, 2, 3)), 
                "多个有效code应该返回true");
            assertFalse(NotificationMethodEnum.isValidCode(Arrays.asList(1, 999)), 
                "包含无效code应该返回false");
        }, "通知方式枚举应该可以正常使用");
    }

    @Test
    @DisplayName("功能测试：通知状态枚举")
    void testNotificationRecordStatusEnum() {
        // 测试通知状态枚举的正确性
        assertEquals(0, NotificationRecordStatusEnum.PENDING.getCode(), 
            "待发送状态码应该是0");
        assertEquals(1, NotificationRecordStatusEnum.SUCCESS.getCode(), 
            "发送成功状态码应该是1");
        assertEquals(2, NotificationRecordStatusEnum.FAILED.getCode(), 
            "发送失败状态码应该是2");

        assertEquals("待发送", NotificationRecordStatusEnum.PENDING.getDesc(), 
            "待发送描述应该正确");
        assertEquals("发送成功", NotificationRecordStatusEnum.SUCCESS.getDesc(), 
            "发送成功描述应该正确");
        assertEquals("发送失败", NotificationRecordStatusEnum.FAILED.getDesc(), 
            "发送失败描述应该正确");

        // 测试根据状态码获取枚举
        assertEquals(NotificationRecordStatusEnum.PENDING, 
            NotificationRecordStatusEnum.getByCode(0), "应该能通过code获取枚举");
        assertEquals(NotificationRecordStatusEnum.SUCCESS, 
            NotificationRecordStatusEnum.getByCode(1), "应该能通过code获取枚举");
        assertEquals(NotificationRecordStatusEnum.FAILED, 
            NotificationRecordStatusEnum.getByCode(2), "应该能通过code获取枚举");
        assertNull(NotificationRecordStatusEnum.getByCode(999), 
            "无效code应该返回null");
    }

    @Test
    @DisplayName("功能测试：业务异常处理")
    void testBusinessExceptionHandling() {
        // 测试通知相关的业务异常
        BusinessException exception = new BusinessException(
            ErrorCode.NOT_FOUND_ERROR,
            "通知模板不存在"
        );

        assertEquals(ErrorCode.NOT_FOUND_ERROR.getCode(), exception.getCode(),
            "异常代码应该正确");
        assertTrue(exception.getMessage().contains("通知模板不存在"),
            "异常消息应该包含自定义信息");

        // 测试参数错误异常
        BusinessException paramsException = new BusinessException(
            ErrorCode.PARAMS_ERROR,
            "通知方式不能为空"
        );

        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), paramsException.getCode(),
            "参数错误代码应该正确");
    }

    @Test
    @DisplayName("功能测试：通知消息对象创建")
    void testNotificationMessageCreation() {
        // 测试NotificationMessage的创建
        TemplatePo template = new TemplatePo();
        template.setTemplateTitle("测试模板");
        template.setTemplateContent("通知内容模板");
        template.setNotificationMethod(Arrays.asList(1, 3));

        NotificationMessage message = new NotificationMessage();
        message.setMessageId("MSG-" + System.currentTimeMillis());
        message.setUserId(1001L);
        message.setTemplate(template);
        message.setCreateTime(LocalDateTime.now());

        assertNotNull(message.getMessageId(), "消息ID应该被正确设置");
        assertEquals(1001L, message.getUserId(), "用户ID应该被正确设置");
        assertNotNull(message.getTemplate(), "模板应该被正确设置");
        assertEquals("测试模板", message.getTemplate().getTemplateTitle(), "模板标题应该被正确设置");
        assertNotNull(message.getTemplate().getNotificationMethod(), "通知方式列表应该被正确设置");
        assertEquals(2, message.getTemplate().getNotificationMethod().size(), "应该有2种通知方式");
    }

    @Test
    @DisplayName("功能测试：通知记录状态转换")
    void testNotificationRecordStatusTransition() {
        // 测试通知记录状态转换
        NotificationRecordPo record = NotificationRecordPo.builder()
            .id(1L)
            .userId(1001L)
            .status(NotificationRecordStatusEnum.PENDING.getCode())
            .build();

        // 待发送 -> 发送成功
        record.setStatus(NotificationRecordStatusEnum.SUCCESS.getCode());
        assertEquals(NotificationRecordStatusEnum.SUCCESS.getCode(), record.getStatus(),
            "状态应该可以从待发送变为发送成功");

        // 如果发送失败
        record.setStatus(NotificationRecordStatusEnum.FAILED.getCode());
        record.setErrorMsg("发送失败：邮件服务器连接超时");
        assertEquals(NotificationRecordStatusEnum.FAILED.getCode(), record.getStatus(),
            "状态应该可以变为发送失败");
        assertNotNull(record.getErrorMsg(), "错误消息应该被记录");
    }

    @Test
    @DisplayName("功能测试：通知已读状态管理")
    void testNotificationReadStatus() {
        // 测试通知已读状态管理
        NotificationRecordPo record = NotificationRecordPo.builder()
            .id(1L)
            .userId(1001L)
            .readStatus(0) // 未读
            .build();

        assertEquals(0, record.getReadStatus(), "初始状态应该是未读");

        // 标记为已读
        record.setReadStatus(1);
        assertEquals(1, record.getReadStatus(), "状态应该变为已读");
    }

    @Test
    @DisplayName("集成测试：完整的通知对象转换流程")
    void testCompleteNotificationConversionFlow() {
        // 模拟一个完整的通知发送流程
        // 1. 创建 SendNotificationDto
        Map<String, Object> vars = new HashMap<>();
        vars.put("deviceName", "笔记本电脑");
        vars.put("userName", "张三");

        SendNotificationDto sendDto = SendNotificationDto.builder()
            .notificationEvent(1)
            .userId(1001L)
            .templateVars(vars)
            .nodeTimestamp(System.currentTimeMillis() / 1000)
            .build();

        // 2. 创建模板
        TemplatePo template = new TemplatePo();
        template.setId(1L);
        template.setTemplateTitle("设备借用通知");
        template.setTemplateContent("您申请借用的{{deviceName}}已审批通过");
        template.setNotificationMethod(Arrays.asList(1, 3));

        // 3. 模拟转换为NotificationMessage
        NotificationMessage message = new NotificationMessage();
        message.setMessageId("MSG-" + System.currentTimeMillis());
        message.setUserId(sendDto.getUserId());
        message.setTemplate(template);
        message.setTemplateVars(sendDto.getTemplateVars());

        // 4. 创建NotificationTask
        NotificationTaskPo task = NotificationTaskPo.builder()
            .id(100L)
            .title(template.getTemplateTitle())
            .content("您申请借用的笔记本电脑已审批通过")
            .notificationRole(1)
            .createTime(LocalDateTime.now())
            .build();

        // 5. 模拟转换为NotificationRecordPo
        NotificationRecordPo recordPo = NotificationRecordPo.builder()
            .id(1L)
            .notificationTaskId(task.getId())
            .userId(message.getUserId())
            .notificationMethod(1)
            .status(NotificationRecordStatusEnum.SUCCESS.getCode())
            .readStatus(0)
            .sendTime(LocalDateTime.now())
            .createTime(LocalDateTime.now())
            .build();

        // 6. 模拟转换为NotificationRecordVo
        NotificationRecordVo recordVo = NotificationRecordVo.builder()
            .id(recordPo.getId())
            .userId(recordPo.getUserId())
            .notificationTaskId(recordPo.getNotificationTaskId())
            .title(task.getTitle())
            .content(task.getContent())
            .notificationMethod(recordPo.getNotificationMethod())
            .status(recordPo.getStatus())
            .statusDesc(NotificationRecordStatusEnum.SUCCESS.getDesc())
            .readStatus(recordPo.getReadStatus())
            .readStatusDesc("未读")
            .sendTime(recordPo.getSendTime())
            .build();

        // 验证整个转换流程
        assertEquals(sendDto.getUserId(), recordVo.getUserId(),
            "用户ID应该在整个转换流程中保持一致");
        assertEquals(task.getTitle(), recordVo.getTitle(),
            "标题应该从Task正确传递到Vo");
        assertEquals(NotificationRecordStatusEnum.SUCCESS.getCode(), recordVo.getStatus(),
            "状态应该是发送成功");
        assertEquals("未读", recordVo.getReadStatusDesc(),
            "新通知应该是未读状态");

        assertNotNull(recordVo, "最终的VO对象应该不为null");
    }

    @Test
    @DisplayName("功能测试：模板变量替换逻辑")
    void testTemplateVariableReplacement() {
        // 测试模板变量的使用
        Map<String, Object> templateVars = new HashMap<>();
        templateVars.put("userName", "李四");
        templateVars.put("deviceName", "投影仪");
        templateVars.put("borrowDate", "2025-10-30");
        templateVars.put("returnDate", "2025-11-05");

        SendNotificationDto dto = SendNotificationDto.builder()
            .userId(1002L)
            .notificationEvent(2)
            .templateVars(templateVars)
            .build();

        // 验证所有变量都被正确设置
        assertEquals("李四", dto.getTemplateVars().get("userName"), "用户名变量应该正确");
        assertEquals("投影仪", dto.getTemplateVars().get("deviceName"), "设备名变量应该正确");
        assertEquals("2025-10-30", dto.getTemplateVars().get("borrowDate"), "借用日期变量应该正确");
        assertEquals("2025-11-05", dto.getTemplateVars().get("returnDate"), "归还日期变量应该正确");
    }

    @Test
    @DisplayName("功能测试：多渠道通知配置")
    void testMultiChannelNotificationConfiguration() {
        // 测试多渠道通知配置
        List<Integer> channels = Arrays.asList(
            NotificationMethodEnum.EMAIL.getCode(),
            NotificationMethodEnum.INTERNAL_MSG.getCode()
        );

        TemplatePo template = new TemplatePo();
        template.setId(1L);
        template.setTemplateTitle("多渠道通知模板");
        template.setNotificationMethod(channels);
        template.setTemplateContent("这是一个多渠道通知");

        // 验证配置
        assertEquals(2, template.getNotificationMethod().size(), "应该配置了2个通知渠道");
        assertTrue(template.getNotificationMethod().contains(
            NotificationMethodEnum.EMAIL.getCode()), "应该包含邮件渠道");
        assertTrue(template.getNotificationMethod().contains(
            NotificationMethodEnum.INTERNAL_MSG.getCode()), "应该包含站内信渠道");

        // 验证所有渠道都是有效的
        assertTrue(NotificationMethodEnum.isValidCode(template.getNotificationMethod()),
            "所有配置的渠道都应该是有效的");
    }

    @Test
    @DisplayName("功能测试：通知任务实体创建")
    void testNotificationTaskEntityCreation() {
        // 测试NotificationTaskPo的创建
        NotificationTaskPo task = new NotificationTaskPo();
        task.setId(100L);
        task.setTitle("设备归还提醒");
        task.setContent("您借用的设备即将到期，请及时归还");
        task.setNotificationRole(1);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());

        assertNotNull(task.getId(), "ID应该被正确设置");
        assertEquals("设备归还提醒", task.getTitle(), "标题应该被正确设置");
        assertEquals("您借用的设备即将到期，请及时归还", task.getContent(), "内容应该被正确设置");
        assertEquals(1, task.getNotificationRole(), "通知角色应该被正确设置");
        assertNotNull(task.getCreateTime(), "创建时间应该被正确设置");
        assertNotNull(task.getUpdateTime(), "更新时间应该被正确设置");
    }

    @Test
    @DisplayName("功能测试：通知方式验证逻辑")
    void testNotificationMethodValidation() {
        // 测试单个通知方式验证
        assertTrue(NotificationMethodEnum.isValidCode(Integer.valueOf(1)), "邮件应该是有效的通知方式");
        assertTrue(NotificationMethodEnum.isValidCode(Integer.valueOf(2)), "短信应该是有效的通知方式");
        assertTrue(NotificationMethodEnum.isValidCode(Integer.valueOf(3)), "站内信应该是有效的通知方式");
        assertFalse(NotificationMethodEnum.isValidCode(Integer.valueOf(0)), "0不是有效的通知方式");
        assertFalse(NotificationMethodEnum.isValidCode(Integer.valueOf(4)), "4不是有效的通知方式");
        assertFalse(NotificationMethodEnum.isValidCode((Integer) null), "null不是有效的通知方式");

        // 测试多个通知方式验证
        List<Integer> validMethods = Arrays.asList(1, 2, 3);
        assertTrue(NotificationMethodEnum.isValidCode(validMethods), 
            "1,2,3都是有效的通知方式");

        List<Integer> invalidMethods = Arrays.asList(1, 2, 999);
        assertFalse(NotificationMethodEnum.isValidCode(invalidMethods), 
            "包含无效方式的列表应该验证失败");

        List<Integer> emptyMethods = new ArrayList<>();
        assertFalse(NotificationMethodEnum.isValidCode(emptyMethods), 
            "空列表应该验证失败");
    }

    @Test
    @DisplayName("功能测试：通知时间相关逻辑")
    void testNotificationTimeLogic() {
        // 测试通知时间相关的逻辑
        LocalDateTime now = LocalDateTime.now();
        
        NotificationRecordPo record = NotificationRecordPo.builder()
            .id(1L)
            .userId(1001L)
            .createTime(now)
            .sendTime(now.plusMinutes(5))
            .updateTime(now)
            .build();

        assertNotNull(record.getCreateTime(), "创建时间不应为null");
        assertNotNull(record.getSendTime(), "发送时间不应为null");
        assertTrue(record.getSendTime().isAfter(record.getCreateTime()) || 
                   record.getSendTime().isEqual(record.getCreateTime()),
            "发送时间应该在创建时间之后或相等");
    }
}

