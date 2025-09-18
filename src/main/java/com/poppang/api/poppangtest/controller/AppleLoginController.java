package com.poppang.api.poppangtest.controller;

// spring
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

// 클래스 안에 있는 final 필드나 @NonNull이 붙은 필드들을 대상으로 생성자를 자동으로 만들어주는 기능
import lombok.RequiredArgsConstructor;
import java.util.Optional;

// local
import com.poppang.api.poppangtest.entity.User;
import com.poppang.api.poppangtest.service.AppleAuthService;
import com.poppang.api.poppangtest.service.UserService;

@RestController
@RequiredArgsConstructor
public class AppleLoginController {

    // 서비스 주입
    private final AppleAuthService appleAuthService;
    private final UserService userService;

    /**
     * ✅ 1. 소셜 로그인 → uid 없으면 DB에 uid만 저장
     */
    @PostMapping("/oauth2/appleLogin")
    public ResponseEntity<?> appleLogin(@RequestParam("authCode") String authCode) {
        try {
            User user = appleAuthService.login(authCode); // 내부에서 findOrCreate 처리
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("❌ Apple login failed: " + e.getMessage());
        }
    }

    /**
     * ✅ 2. 온보딩 단계에서 닉네임 업데이트
     */
    @PutMapping("/oauth2/users/{uid}/nickname")
    public ResponseEntity<?> updateNickname(
            @PathVariable String uid,
            @RequestParam String nickname
    ) {
        Optional<User> userOpt = userService.findUserByUid(uid);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ User not found with uid=" + uid);
        }

        User user = userOpt.get();
        user.setNickname(nickname);

        User updatedUser = userService.saveUser(user); // UserService에 save 추가 필요
        return ResponseEntity.ok(updatedUser);
    }
}