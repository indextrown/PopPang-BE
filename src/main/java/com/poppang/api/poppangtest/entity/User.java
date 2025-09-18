package com.poppang.api.poppangtest.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity                 // JPA 엔티티임을 표시 (테이블 매핑)
@Table(name = "users")  // 실제 DB 테이블 이름: users
@Getter
@Setter
@NoArgsConstructor            // 기본 생성자 자동 생성
@AllArgsConstructor           // 모든 필드 생성자 자동 생성
@Builder                      // 빌더 패턴 지원
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // AUTO_INCREMENT 매핑
    private Long id;   // 유저 PK (BIGINT)

    @Column(nullable = false, unique = true, length = 255)
    private String uid;   // 소셜 로그인 유저 ID (Google/Apple/Kakao 등)

    @Column(nullable = true, length = 255)
    private String nickname;   // 닉네임 (NULL 허용)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;   // 회원/관리자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;   // 가입 플랫폼

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "is_alerted", nullable = false)
    private boolean isAlerted = false;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ENUM 정의
    public enum Role {
        member, admin
    }

    public enum Provider {
        google, apple, kakao
    }
}
