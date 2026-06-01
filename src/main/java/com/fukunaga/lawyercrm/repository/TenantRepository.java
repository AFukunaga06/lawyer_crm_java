package com.fukunaga.lawyercrm.repository;

import com.fukunaga.lawyercrm.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
}
