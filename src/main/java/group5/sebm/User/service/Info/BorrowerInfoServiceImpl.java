package group5.sebm.User.service.Info;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.User.dao.BorrowerInfoMapper;
import group5.sebm.User.entity.BorrowerInfoPo;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author Luoimo
* @description 针对表【borrower_info(借用者)】的数据库操作Service实现
* @createDate 2025-09-28 15:48:22
*/
@Service
@AllArgsConstructor
public class BorrowerInfoServiceImpl extends ServiceImpl<BorrowerInfoMapper, BorrowerInfoPo>
    implements BorrowerInfoService{

  private final BorrowerInfoMapper borrowerInfoMapper;
  /**
   * 增加逾期次数
   * @param userId
   * @return
   */
  @Override
  public Integer addOverdueTimes(Long userId) {
    //1.先查询用户是否存在
    QueryWrapper<BorrowerInfoPo> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("userId", userId);
    BorrowerInfoPo borrowerInfoPo = borrowerInfoMapper.selectOne(queryWrapper);
    ThrowUtils.throwIf(borrowerInfoPo == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
    //2.增加逾期次数
    borrowerInfoPo.setOverdueTimes(borrowerInfoPo.getOverdueTimes() + 1);
    //3.更新借用者信息
    borrowerInfoMapper.updateById(borrowerInfoPo);
    //4.返回逾期次数
    return borrowerInfoPo.getOverdueTimes();
  }
  /**
   * 更新借出设备数量
   * @param userId
   * @param num
   * @return
   */
  @Override
  public Integer updateBorrowedCount(Long userId,Integer num) {
    //1. 先查询用户是否存在
    QueryWrapper<BorrowerInfoPo> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("userId", userId);
    BorrowerInfoPo borrowerInfoPo = borrowerInfoMapper.selectOne(queryWrapper);

    ThrowUtils.throwIf(borrowerInfoPo == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
    //2. 更新借出设备数量
    borrowerInfoPo.setBorrowedDeviceCount(borrowerInfoPo.getBorrowedDeviceCount() + num);
    //3. 更新借用者信息
    borrowerInfoMapper.updateById(borrowerInfoPo);
    //4. 返回借出设备数量
    return borrowerInfoPo.getBorrowedDeviceCount();
  }
}




