package group5.sebm.notifiation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.common.constant.NotificationConstant;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.dto.TemplateQueryDto;
import group5.sebm.notifiation.controller.vo.TemplateVo;
import group5.sebm.notifiation.converter.TemplateConverter;
import group5.sebm.notifiation.dao.TemplateMapper;
import group5.sebm.notifiation.entity.TemplatePo;
import group5.sebm.notifiation.service.TemplateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知模板服务实现类
 */
@Slf4j
@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, TemplatePo> implements TemplateService {
    
    @Autowired
    private TemplateConverter templateConverter;

    /**
     * 创建通知模板
     * @param createTemplateDto 创建模板请求DTO
     * @param request HTTP请求对象
     * @return 创建的模板VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TemplateVo createTemplate(CreateTemplateDto createTemplateDto, HttpServletRequest request) {
        log.info("1.before createTemplate execute，params：{}", createTemplateDto);

        // DTO 转 PO
        TemplatePo templatePo = templateConverter.toPo(createTemplateDto, request);
        
        // 插入数据库
        boolean insertResult = this.save(templatePo);
        ThrowUtils.throwIf(!insertResult, ErrorCode.OPERATION_ERROR, "模板创建失败");
        
        log.info("2.create template success, template id：{}, notification event: {}",
                templatePo.getId(), createTemplateDto.getNotificationEvent());
        
        // PO 转 VO 并返回
        return templateConverter.toVo(templatePo);
    }

    /**
     * 分页查询模板列表
     * @param templateQueryDto 查询条件
     * @return 分页结果
     */
    @Override
    public Page<TemplateVo> getTemplateList(TemplateQueryDto templateQueryDto) {
        log.info("查询模板列表，参数：{}", templateQueryDto);
        
        // 1. 创建分页对象
        Page<TemplatePo> page = new Page<>(templateQueryDto.getPageNumber(), templateQueryDto.getPageSize());
        
        // 2. 构建查询条件
        QueryWrapper<TemplatePo> queryWrapper = buildQueryWrapper(templateQueryDto);
        
        // 3. 执行分页查询
        Page<TemplatePo> templatePage = this.page(page, queryWrapper);
        
        // 4. 将 PO 转 VO
        List<TemplateVo> voList = templatePage.getRecords().stream()
                .map(templateConverter::toVo)
                .collect(Collectors.toList());
        
        // 5. 构建返回结果
        Page<TemplateVo> resultPage = new Page<>(templatePage.getCurrent(), templatePage.getSize(), templatePage.getTotal());
        resultPage.setRecords(voList);
        
        log.info("查询模板列表完成，总数：{}，当前页：{}", templatePage.getTotal(), templatePage.getCurrent());
        return resultPage;
    }
    
    /**
     * 根据通知参数查询模板
     * @param notificationEvent 通知code
     * @return 模板实体
     */
    @Override
    public TemplatePo findTemplateByParams( Integer notificationEvent) {
        log.info("查询默认模板 - 通知code: {}", notificationEvent);

        QueryWrapper<TemplatePo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("notificationEvent", notificationEvent)
                .eq("isDelete", NotificationConstant.NOT_DELETED)
                .eq("status", NotificationConstant.TEMPLATE_STATUS_ACTIVE);

        queryWrapper.orderByDesc("createTime")
                .last("LIMIT 1");

        TemplatePo template = this.getOne(queryWrapper);

        if (template == null) {
            log.warn("未找到通知 {} 的模板", notificationEvent);
        } else {
            log.info("找到默认模板: ID={}, 标题={}", template.getId(), template.getTemplateTitle());
        }

        return template;
    }
    
    /**
     * 构建查询条件
     * @param templateQueryDto 查询参数
     * @return QueryWrapper
     */
    private QueryWrapper<TemplatePo> buildQueryWrapper(TemplateQueryDto templateQueryDto) {
        QueryWrapper<TemplatePo> queryWrapper = new QueryWrapper<>();
        
        // 基础条件：未删除
        queryWrapper.eq("isDelete", NotificationConstant.NOT_DELETED);
        
        // 模板标题模糊查询
        if (StringUtils.hasText(templateQueryDto.getTemplateTitle())) {
            queryWrapper.like("templateTitle", templateQueryDto.getTemplateTitle());
        }
        
        // 通知节点
        if (templateQueryDto.getNotificationNode() != null) {
            queryWrapper.eq("notificationNode", templateQueryDto.getNotificationNode());
        }
        
        // 通知方式
        if (templateQueryDto.getNotificationMethod() != null) {
            queryWrapper.eq("notificationMethod", templateQueryDto.getNotificationMethod());
        }
        
        // 模板状态
        if (StringUtils.hasText(templateQueryDto.getStatus())) {
            queryWrapper.eq("status", templateQueryDto.getStatus());
        }
        
        // 创建者ID
        if (templateQueryDto.getUserId() != null) {
            queryWrapper.eq("userId", templateQueryDto.getUserId());
        }
        
        return queryWrapper;
    }
}
