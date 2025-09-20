package com.poppang.api.poppangtest.service;

import com.poppang.api.poppangtest.entity.User;
import com.poppang.api.poppangtest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 신규 생성
    public User createUser(String uid, String provider, String email) {
        User newUser = User.builder()
                .uid(uid)
                .provider(User.Provider.valueOf(provider))  // enum 변환
                .email(email)
                .role(User.Role.member)  // 기본값 설정 가능
                .build();
        return userRepository.save(newUser);
    }

    // UID 기반 조회
    public Optional<User> findUserByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    // ✅ 추가: 닉네임 등 수정 후 저장
    public User saveUser(User user) {
        return userRepository.save(user);
    }

}