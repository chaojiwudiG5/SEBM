package group5.sebm.User.service.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import group5.sebm.User.controller.vo.MechanicVo;
import group5.sebm.User.entity.MechanicInfoPo;
import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.Info.MechanicInfoService;
import group5.sebm.common.dto.User.MechanicInfoDto;
import group5.sebm.common.dto.User.UserInfoDto;
import group5.sebm.common.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 技工用户信息策略
 * 
 * @author System
 */
@Component
@AllArgsConstructor
public class MechanicInfoStrategy implements UserInfoStrategy {
    
    private final MechanicInfoService mechanicInfoService;
    
    @Override
    public int getRoleCode() {
        return UserRoleEnum.TECHNICIAN.getCode();
    }
    
    @Override
    public MechanicVo buildUserVo(UserPo userPo) {
        MechanicVo mechanicVo = new MechanicVo();
        MechanicInfoPo mechanicInfoPo = mechanicInfoService.getOne(
            new QueryWrapper<MechanicInfoPo>().eq("userId", userPo.getId()));

        BeanUtils.copyProperties(mechanicInfoPo, mechanicVo);

        BeanUtils.copyProperties(userPo, mechanicVo);
        return mechanicVo;
    }
    
    @Override
    public UserInfoDto buildUserInfoDto(UserPo userPo) {
        MechanicInfoPo mechanicInfoPo = mechanicInfoService.getOne(
            new QueryWrapper<MechanicInfoPo>().eq("userId", userPo.getId()));
        
        MechanicInfoDto mechanicInfoDto = new MechanicInfoDto();
        BeanUtils.copyProperties(mechanicInfoPo, mechanicInfoDto);
        BeanUtils.copyProperties(userPo, mechanicInfoDto);
        
        return mechanicInfoDto;
    }
}
