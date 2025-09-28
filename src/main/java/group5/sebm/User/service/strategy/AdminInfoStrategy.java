package group5.sebm.User.service.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import group5.sebm.User.controller.vo.AdminVo;
import group5.sebm.User.entity.AdminInfoPo;
import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.Info.AdminInfoService;
import group5.sebm.common.dto.User.AdminInfoDto;
import group5.sebm.common.dto.User.UserInfoDto;
import group5.sebm.common.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 管理员用户信息策略
 * 
 * @author System
 */
@Component
@AllArgsConstructor
public class AdminInfoStrategy implements UserInfoStrategy {
    
    private final AdminInfoService adminInfoService;
    
    @Override
    public int getRoleCode() {
        return UserRoleEnum.ADMIN.getCode();
    }
    
    @Override
    public AdminVo buildUserVo(UserPo userPo) {
        AdminVo adminVo = new AdminVo();
        AdminInfoPo adminInfoPo = adminInfoService.getOne(
            new QueryWrapper<AdminInfoPo>().eq("userId", userPo.getId()));
        
        BeanUtils.copyProperties(userPo, adminVo);
        BeanUtils.copyProperties(adminInfoPo, adminVo);
        
        return adminVo;
    }
    
    @Override
    public UserInfoDto buildUserInfoDto(UserPo userPo) {
        AdminInfoPo adminInfoPo = adminInfoService.getOne(
            new QueryWrapper<AdminInfoPo>().eq("userId", userPo.getId()));
        
        AdminInfoDto adminInfoDto = new AdminInfoDto();
        BeanUtils.copyProperties(adminInfoPo, adminInfoDto);
        BeanUtils.copyProperties(userPo, adminInfoDto);
        
        return adminInfoDto;
    }
}

