package group5.sebm.service;

import static group5.sebm.common.constant.UserConstant.CURRENT_LOGIN_USER;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import group5.sebm.controller.dto.DeleteDto;
import group5.sebm.controller.dto.PageDto;
import group5.sebm.controller.dto.LoginDto;
import group5.sebm.controller.dto.RegisterDto;
import group5.sebm.controller.dto.UpdateDto;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.dao.UserMapper;
import group5.sebm.entity.UserPo;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import group5.sebm.service.bo.Borrower;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;


import java.util.List;
import java.util.stream.Collectors;

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