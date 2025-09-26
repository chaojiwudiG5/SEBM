package group5.sebm.notifiation.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.notifiation.controller.dto.CreateTemplateDto;
import group5.sebm.notifiation.controller.vo.TemplateVo;
import group5.sebm.notifiation.converter.TemplateConverter;
import group5.sebm.notifiation.dao.TemplateDao;
import group5.sebm.notifiation.entity.TemplatePo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知模板服务实现类
 */
@Slf4j
@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateDao, TemplatePo> implements TemplateService {
    
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
        
        log.info("2.create template success, template id：{}", templatePo.getId());
        
        // PO 转 VO 并返回
        return templateConverter.toVo(templatePo);
    }
}
