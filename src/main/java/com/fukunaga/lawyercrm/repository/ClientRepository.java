package com.fukunaga.lawyercrm.repository;

import com.fukunaga.lawyercrm.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    Optional<Client> findByIdAndTenantId(Long id, Long tenantId);
    long countByTenantId(Long tenantId);
}
