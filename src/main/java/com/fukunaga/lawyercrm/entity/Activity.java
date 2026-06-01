package com.fukunaga.lawyercrm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** 活動記録（電話・面談・メール等）。依頼者必須、案件は任意。 */
@Entity
@Table(name = "lc_activities")
public class Activity {

    public enum Type { phone, meeting, email, document, court, other }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 20)
    private Type activityType = Type.other;

    @Column(name = "activity_at", nullable = false)
    private LocalDateTime activityAt;

    @Column(name = "duration_min")
    private Integer durationMin;

    @Column(length = 200)
    private String title = "";

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String result;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Type getActivityType() { return activityType; }
    public void setActivityType(Type activityType) { this.activityType = activityType; }
    public LocalDateTime getActivityAt() { return activityAt; }
    public void setActivityAt(LocalDateTime activityAt) { this.activityAt = activityAt; }
    public Integer getDurationMin() { return durationMin; }
    public void setDurationMin(Integer durationMin) { this.durationMin = durationMin; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
