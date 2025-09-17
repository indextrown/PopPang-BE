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
    @Column(nullable = false, unique = true, length = 100)
    private String id;              // ✅ Apple sub / Google sub / Kakao id

    @Column(nullable = false, length = 20)
    private String provider;        // 가입 플랫폼 (apple, google, kakao)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    // 최초 insert 시 자동 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    // update 시 자동 설정
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}