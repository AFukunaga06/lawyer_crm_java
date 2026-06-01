package com.fukunaga.lawyercrm.entity;

import com.fukunaga.lawyercrm.vertical.Vertical;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/** テナント（事務所）マスタ。マルチテナントの最上位。 */
@Entity
@Table(name = "lc_tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    /** 業種。LAWYER / TAX_ACCOUNTANT / JUDICIAL_SCRIVENER / ADMINISTRATIVE_SCRIVENER */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Vertical vertical = Vertical.LAWYER;

    @Column(length = 30)
    private String plan = "solo";

    @Column(length = 20)
    private String status = "active";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Vertical getVertical() { return vertical; }
    public void setVertical(Vertical vertical) { this.vertical = vertical; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
