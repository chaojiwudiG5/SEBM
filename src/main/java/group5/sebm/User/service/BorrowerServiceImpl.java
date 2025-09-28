package group5.sebm.User.service;

import group5.sebm.User.entity.UserPo;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@NoArgsConstructor
public class BorrowerServiceImpl extends UserServiceImpl implements BorrowerService {

  /**
   * 增加逾期次数
   * @param userId
   * @return
   */
  @Override
  public Integer addOverdueTimes(Long userId) {
    UserPo userPo = this.getById(userId);
    if (userPo == null) {
      ThrowUtils.throwIf(true, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
    }
    userPo.setOverdueTimes(userPo.getOverdueTimes() + 1);
    this.updateById(userPo);
    return userPo.getOverdueTimes();
  }
  /**
   * 更新借出设备数量
   * @param userId
   * @param num
   * @return
   */
  @Override
  public Integer updateBorrowedCount(Long userId,Integer num) {
    //1.先查询用户是否存在
    UserPo userPo = this.getById(userId);
    if (userPo == null) {
      ThrowUtils.throwIf(true, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
    }
    //2.增加借出设备数量
    userPo.setBorrowedDeviceCount(userPo.getBorrowedDeviceCount() + num);
    //3.更新用户信息
    this.updateById(userPo);
    //4.返回借出设备数量
    return userPo.getBorrowedDeviceCount();
  }


}