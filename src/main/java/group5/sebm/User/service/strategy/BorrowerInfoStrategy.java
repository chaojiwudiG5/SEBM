package group5.sebm.User.service.strategy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import group5.sebm.User.controller.vo.BorrowerVo;
import group5.sebm.User.entity.BorrowerInfoPo;
import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.Info.BorrowerInfoService;
import group5.sebm.common.dto.User.BorrowerInfoDto;
import group5.sebm.common.dto.User.UserInfoDto;
import group5.sebm.common.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 借用者用户信息策略
 *
 * @author System
 */
@Component
@AllArgsConstructor
public class BorrowerInfoStrategy implements UserInfoStrategy {

  private final BorrowerInfoService borrowerInfoService;

  @Override
  public int getRoleCode() {
    return UserRoleEnum.USER.getCode();
  }

  @Override
  public BorrowerVo buildUserVo(UserPo userPo) {
    BorrowerVo borrowerVo = new BorrowerVo();
    BorrowerInfoPo borrowerInfoPo = borrowerInfoService.getOne(
        new QueryWrapper<BorrowerInfoPo>().eq("userId", userPo.getId()));

    BeanUtils.copyProperties(borrowerInfoPo, borrowerVo);
    BeanUtils.copyProperties(userPo, borrowerVo);
    return borrowerVo;
  }

  @Override
  public UserInfoDto buildUserInfoDto(UserPo userPo) {
    BorrowerInfoPo borrowerInfoPo = borrowerInfoService.getOne(
        new QueryWrapper<BorrowerInfoPo>().eq("userId", userPo.getId()));

    BorrowerInfoDto borrowerInfoDto = new BorrowerInfoDto();
    BeanUtils.copyProperties(borrowerInfoPo, borrowerInfoDto);
    BeanUtils.copyProperties(userPo, borrowerInfoDto);

    return borrowerInfoDto;
  }
}

