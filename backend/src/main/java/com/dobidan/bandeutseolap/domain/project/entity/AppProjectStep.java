package com.dobidan.bandeutseolap.domain.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "app_project_step")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AppProjectStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private AppProject project;

    @Size(max = 255)
    @NotNull
    @Column(name = "step_name", nullable = false)
    private String stepName;

    @Size(max = 30)
    @NotNull
    @Column(name = "step_type_cd", nullable = false, length = 30)
    private String stepTypeCd;

    @Size(max = 500)
    @Column(name = "step_desc", length = 500)
    private String stepDesc;

    @Column(name = "step_order", columnDefinition = "int UNSIGNED not null")
    private Long stepOrder;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_step_id")

    private AppProjectStep parentStep;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Size(max = 30)
    @NotNull
    @Column(name = "step_status_cd", nullable = false, length = 30)
    private String stepStatusCd;

    @NotNull
    @Column(name = "planned_start_dt", nullable = false)
    private LocalDate plannedStartDt;

    @NotNull
    @Column(name = "planned_end_dt", nullable = false)
    private LocalDate plannedEndDt;

    @Column(name = "actual_start_dt")
    private LocalDate actualStartDt;

    @Column(name = "actual_end_dt")
    private LocalDate actualEndDt;

    @NotNull
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}