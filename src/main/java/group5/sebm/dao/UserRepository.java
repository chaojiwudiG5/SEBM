package group5.sebm.dao;

import group5.sebm.entity.UserPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserPo, Integer> {
    Optional<UserPo> findByid(Integer id);
    // save() / findById() / findAll() / deleteById() / existsById()
}