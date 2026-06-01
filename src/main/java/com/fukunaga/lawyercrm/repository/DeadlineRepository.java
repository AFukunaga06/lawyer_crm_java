package com.fukunaga.lawyercrm.repository;

import com.fukunaga.lawyercrm.entity.Deadline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeadlineRepository extends JpaRepository<Deadline, Long> {
    List<Deadline> findByTenantIdAndCaseIdOrderByDeadlineDateAsc(Long tenantId, Long caseId);
    List<Deadline> findByTenantIdAndDoneFalseOrderByDeadlineDateAsc(Long tenantId);
    List<Deadline> findByTenantIdAndDoneFalseAndDeadlineDateBetweenOrderByDeadlineDateAsc(
            Long tenantId, LocalDateTime from, LocalDateTime to);
    Optional<Deadline> findByIdAndTenantId(Long id, Long tenantId);
}
