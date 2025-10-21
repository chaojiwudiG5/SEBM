package group5.sebm.User.service;

import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.UserServiceInterface.BorrowerService;
import group5.sebm.User.service.bo.Borrower;
import group5.sebm.User.service.bo.User;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@NoArgsConstructor
public class BorrowerServiceImpl extends UserServiceImpl implements BorrowerService {

  @Override
  public Boolean updateBorrowedCount(Long userId, Integer borrowedCount) {
    if (userId == null || borrowedCount == null) {
      throw new BusinessException(ErrorCode.PARAMS_ERROR, "userId and borrowedCount are required");
    }

    UserPo userPo = this.getById(userId);
    if (userPo == null) {
      throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "User not found");
    }

    try {
      Borrower user = new Borrower();
      BeanUtils.copyProperties(userPo, user);
      user.updateBorrowedCount(borrowedCount);
      userPo.setBorrowedDeviceCount(user.getBorrowedDeviceCount());
      this.updateById(userPo);
      return true;

    } catch (Exception e) {
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update borrowedCount failed"+e.getMessage());
    }
  }


}