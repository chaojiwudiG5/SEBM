package group5.sebm.User.service.strategy;

import group5.sebm.exception.BusinessException;
import group5.sebm.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户信息策略工厂
 * 根据用户角色获取对应的策略实现
 * 
 * @author System
 */
@Component
@AllArgsConstructor
public class UserInfoStrategyFactory {
    
    private final List<UserInfoStrategy> strategies;
    
    private Map<Integer, UserInfoStrategy> strategyMap;
    
    /**
     * 初始化策略映射
     */
    @PostConstruct
    public void initStrategyMap() {
        strategyMap = strategies.stream()
            .collect(Collectors.toMap(
                UserInfoStrategy::getRoleCode,
                Function.identity()
            ));
    }
    
    /**
     * 根据角色代码获取对应的策略
     * @param roleCode 角色代码
     * @return 用户信息策略
     */
    public UserInfoStrategy getStrategy(int roleCode) {
        UserInfoStrategy strategy = strategyMap.get(roleCode);
        if (strategy == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Unknown user role: " + roleCode);
        }
        return strategy;
    }
}
