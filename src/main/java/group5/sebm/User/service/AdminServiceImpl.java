package group5.sebm.User.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import group5.sebm.User.controller.dto.DeleteDto;
import group5.sebm.User.controller.dto.PageDto;
import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.dao.UserMapper;
import group5.sebm.User.entity.UserPo;
import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import group5.sebm.exception.ThrowUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Version;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
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
     * 批量删除用户
     */
    @Override
    public Boolean deleteBorrowers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id list is empty");
        }
        try {
            userMapper.deleteByIds(ids);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Batch delete failed");
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
        Page<UserPo> userPage = userMapper.selectPage(page, new QueryWrapper<>());

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
