package group5.sebm.User.service.strategy;

import group5.sebm.User.controller.vo.UserVo;
import group5.sebm.User.entity.UserPo;
import group5.sebm.common.dto.User.UserInfoDto;

/**
 * 用户信息策略接口
 * 用于根据不同用户角色处理用户信息
 * 
 * @author System
 */
public interface UserInfoStrategy {
    
    /**
     * 获取用户角色代码
     * @return 角色代码
     */
    int getRoleCode();
    
    /**
     * 根据用户信息构建UserVo对象
     * @param userPo 用户基础信息
     * @return UserVo对象
     */
    UserVo buildUserVo(UserPo userPo);
    
    /**
     * 根据用户信息构建UserInfoDto对象
     * @param userPo 用户基础信息
     * @return UserInfoDto对象
     */
    UserInfoDto buildUserInfoDto(UserPo userPo);
    
    /**
     * 为用户登录设置token
     * @param userVo UserVo对象
     * @param token JWT token
     */
    default void setToken(UserVo userVo, String token) {
        userVo.setToken(token);
    }
}

