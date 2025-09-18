package com.poppang.api.poppangtest.repository;

import com.poppang.api.poppangtest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository는 JAP에서 제공하는 기본 인터페이스이다 <엔티티, 엔티티의pk>를 받는다
// DB(User 테이블)와 연결되는 인터페이스
// 직접 SQL을 작성하지 않아도 기본 CRUD 메서드를 제공한다
public interface UserRepository extends JpaRepository<User, String> {
    // JpaRepository<User, String> 이므로 findById() 메서드를 바로 사용 가능
    // findById(String id)
    // findAll()
    // save(User user)
    // delete(User user)
    // count()
    Optional<User> findByUid(String uid);   // uid 기반 조회
}
