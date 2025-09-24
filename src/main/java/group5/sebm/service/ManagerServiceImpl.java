package group5.sebm.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.controller.dto.*;
import group5.sebm.controller.vo.UserVo;
import group5.sebm.dao.UserMapper;
import group5.sebm.entity.UserPo;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static group5.sebm.common.constant.UserConstant.CURRENT_LOGIN_USER;
@Service
public class ManagerServiceImpl extends UserServiceImpl {
    UserMapper userMapper;
    /**
     * 删除用户
     *
     * @param deleteDto 用户id
     * @return 是否删除成功
     */
    public Boolean deleteBorrower(DeleteDto deleteDto) {
        //1. check if user exists
        UserPo userPo = userMapper.selectById(deleteDto.getId());
        ThrowUtils.throwIf(userPo == null, ErrorCode.NOT_FOUND_ERROR, "User not exists");
        //2. delete user from database
        try {
            userMapper.deleteById(deleteDto.getId());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Delete failed");
        }
        return true;
    }

    /**
     * 获取所有用户
     *
     * @param pageDto 分页信息
     * @return 用户列表
     */

    public Page<UserVo> getAllBorrowers(PageDto pageDto) {
        // 1. 创建分页对象
        Page<UserPo> page = new Page<>(pageDto.getPageNumber(), pageDto.getPageSize());

        // 2. 执行分页查询
        Page<UserPo> userPage = baseMapper.selectPage(page, new QueryWrapper<>());

        // 3. 将 PO 转 VO
        List<UserVo> voList = userPage.getRecords().stream()
                .map(po -> {
                    UserVo vo = new UserVo();
                    BeanUtils.copyProperties(po, vo);
                    return vo;
                })
                .collect(Collectors.toList());


        // 4. 将 VO 列表放回 Page 对象
        Page<UserVo> resultPage = new Page<>(userPage.getCurrent(), userPage.getSize(),
                userPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }
}
