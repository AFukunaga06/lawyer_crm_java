package com.fukunaga.lawyercrm.repository;

import com.fukunaga.lawyercrm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTenantIdAndLoginId(Long tenantId, String loginId);
    Optional<User> findByLoginId(String loginId);
    List<User> findByTenantIdOrderByIdAsc(Long tenantId);
}
