package com.fukunaga.lawyercrm.repository;

import com.fukunaga.lawyercrm.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByTenantIdAndClientIdOrderByActivityAtDesc(Long tenantId, Long clientId);
    List<Activity> findByTenantIdAndCaseIdOrderByActivityAtDesc(Long tenantId, Long caseId);
    List<Activity> findTop20ByTenantIdOrderByActivityAtDesc(Long tenantId);
}
