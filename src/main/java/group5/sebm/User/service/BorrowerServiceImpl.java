package group5.sebm.User.service;

import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.entity.UserPo;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@NoArgsConstructor
public class BorrowerServiceImpl extends UserServiceImpl {

  /**
   * 用户注销
   *
   * @param deleteDto 用户id
   * @return 是否删除成功
   */
  public Boolean deactivateUser(DeleteDto deleteDto) {
    // 1. 检查用户是否存在
    UserPo userPo = baseMapper.selectById(deleteDto.getId());
    ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");

    // 2. 修改用户状态为“已注销”，假设 status=0 表示正常，status=1 表示注销
    userPo.setIsDelete(1);
    try {
      baseMapper.updateById(userPo);
    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Deactivate failed");
    }

    return true;
  }



}