package com.dobidan.bandeutseolap.domain.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "app_project")
public class AppProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id", nullable = false)
    private Long id;

    @Size(max = 14)
    @NotNull
    @Column(name = "project_code", nullable = false, length = 14)
    private String projectCode;

    @Size(max = 100)
    @NotNull
    @Column(name = "project_name", nullable = false, length = 100)
    private String projectName;

    @NotNull
    @Lob
    @Column(name = "project_desc", nullable = false)
    private String projectDesc;

    @Column(name = "current_step_id")
    private Long currentStepId;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Size(max = 30)
    @NotNull
    @Column(name = "project_status_cd", nullable = false, length = 30)
    private String projectStatusCd;

    @Size(max = 30)
    @NotNull
    @Column(name = "project_type_cd", nullable = false, length = 30)
    private String projectTypeCd;

    @Size(max = 30)
    @NotNull
    @Column(name = "visibility_cd", nullable = false, length = 30)
    private String visibilityCd;

    @NotNull
    @Column(name = "manager_user_id", nullable = false)
    private Long managerUserId;

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
    @ColumnDefault("0")
    @Column(name = "archive_yn", nullable = false)
    private Byte archiveYn;

    @Column(name = "archived_at")
    private Instant archivedAt;

    @NotNull
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


}