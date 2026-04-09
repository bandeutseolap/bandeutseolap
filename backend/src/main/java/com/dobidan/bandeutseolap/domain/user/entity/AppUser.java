package com.dobidan.bandeutseolap.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "app_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @Column(name = "lgn_id", nullable = false, length = 50)
    private String lgnId;

    @Size(max = 255)
    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Size(max = 100)
    @NotNull
    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 40)
    @Column(name = "mobile_phone", length = 40)
    private String mobilePhone;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'ACTIVE'")
    @Column(name = "acc_status_cd", nullable = false, length = 30)
    @Builder.Default
    private String accStatusCd = "ACTIVE";

    @Column(name = "last_lgn_at")
    private LocalDateTime lastLgnAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw() {
        this.deletedAt = LocalDateTime.now();  // 직접 넣어줌
        this.accStatusCd = "WITHDRAWN";
    }

}