package com.dobidan.bandeutseolap.domain.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // PRIVATE 추가
@Builder
@Table(name = "app_task")
public class AppTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private AppProject project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "step_id", nullable = false)
    private AppProjectStep step;

    @Size(max = 255)
    @NotNull
    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "start_dt")
    private LocalDate startDt;

    @Column(name = "end_dt")
    private LocalDate endDt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private AppProjectMember member;

    @Column(name = "priority", columnDefinition = "int UNSIGNED not null")
    private Long priority;

    @Size(max = 30)
    @NotNull
    @Column(name = "task_difficulty_cd", nullable = false, length = 30)
    private String taskDifficultyCd;

    @Size(max = 30)
    @NotNull
    @Column(name = "task_type_cd", nullable = false, length = 30)
    private String taskTypeCd;

    @Size(max = 30)
    @NotNull
    @Column(name = "task_status_cd", nullable = false, length = 30)
    private String taskStatusCd;

    @Column(name = "man_month", precision = 4, scale = 2)
    private BigDecimal manMonth;

    @NotNull
    @ColumnDefault("0")
    @Builder.Default
    @Column(name = "progress", nullable = false)
    private Integer progress = 0;

}