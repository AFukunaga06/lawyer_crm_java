package com.fukunaga.lawyercrm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 期日管理。案件にひもづく。 */
@Entity
@Table(name = "lc_deadlines")
public class Deadline {

    public enum Type { court, document, meeting, payment, other }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "deadline_date", nullable = false)
    private LocalDateTime deadlineDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "deadline_type", nullable = false, length = 20)
    private Type deadlineType = Type.other;

    @Column(name = "is_done", nullable = false)
    private boolean done = false;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getDeadlineDate() { return deadlineDate; }
    public void setDeadlineDate(LocalDateTime deadlineDate) { this.deadlineDate = deadlineDate; }
    public Type getDeadlineType() { return deadlineType; }
    public void setDeadlineType(Type deadlineType) { this.deadlineType = deadlineType; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
