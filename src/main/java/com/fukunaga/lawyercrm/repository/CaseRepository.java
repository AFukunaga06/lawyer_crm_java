package com.fukunaga.lawyercrm.repository;

import com.fukunaga.lawyercrm.entity.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CaseRepository extends JpaRepository<LegalCase, Long> {
    List<LegalCase> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    List<LegalCase> findByTenantIdAndClientIdOrderByCreatedAtDesc(Long tenantId, Long clientId);
    Optional<LegalCase> findByIdAndTenantId(Long id, Long tenantId);
    long countByTenantId(Long tenantId);
    long countByTenantIdAndStatus(Long tenantId, LegalCase.Status status);
}
