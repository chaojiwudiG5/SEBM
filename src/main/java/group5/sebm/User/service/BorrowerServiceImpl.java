package group5.sebm.User.service;

import group5.sebm.User.entity.UserPo;
import group5.sebm.User.service.UserServiceInterface.BorrowerService;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@NoArgsConstructor
public class BorrowerServiceImpl extends UserServiceImpl implements BorrowerService {

  @Override
  public Boolean updateBorrowedCount(Long userId, Integer borrowedCount) {
    //1. get user by id
    UserPo user = this.getById(userId);
    //2. update borrowedCount
    try {
      Integer borrowedDeviceCount = user.getBorrowedDeviceCount();
      user.setBorrowedDeviceCount(borrowedDeviceCount + borrowedCount);
      this.updateById(user);
    }catch (Exception e){
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update borrowedCount failed");
    }
    return true;
  }
}