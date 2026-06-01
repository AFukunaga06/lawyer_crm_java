package com.fukunaga.lawyercrm.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 請求管理（着手金・報酬金・実費等）。案件にひもづく。 */
@Entity
@Table(name = "lc_billing")
public class Billing {

    public enum Type { retainer, success, hourly, expense, other }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_type", nullable = false, length = 20)
    private Type billingType = Type.retainer;

    @Column(length = 200)
    private String title = "";

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(precision = 6, scale = 2)
    private BigDecimal hours;

    @Column(name = "billed_date")
    private LocalDate billedDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "is_paid", nullable = false)
    private boolean paid = false;

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
    public Type getBillingType() { return billingType; }
    public void setBillingType(Type billingType) { this.billingType = billingType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getHours() { return hours; }
    public void setHours(BigDecimal hours) { this.hours = hours; }
    public LocalDate getBilledDate() { return billedDate; }
    public void setBilledDate(LocalDate billedDate) { this.billedDate = billedDate; }
    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
