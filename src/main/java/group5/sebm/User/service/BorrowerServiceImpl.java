package group5.sebm.User.service;

import group5.sebm.User.service.UserServiceInterface.BorrowerService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@NoArgsConstructor
public class BorrowerServiceImpl extends UserServiceImpl implements BorrowerService {
}