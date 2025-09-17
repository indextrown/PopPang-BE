package com.poppang.api.poppangtest.controller;

// spring
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
// 클래스 안에 있는 final 필드나 @NonNull이 붙은 필드들을 대상으로 생성자를 자동으로 만들어주는 기능
import lombok.RequiredArgsConstructor;
import com.nimbusds.jwt.JWTClaimsSet;


// local
import com.poppang.api.poppangtest.dto.User;
import com.poppang.api.poppangtest.dto.AppleTokenResponse;
import com.poppang.api.poppangtest.service.AppleAuthService;

@RestController
@RequiredArgsConstructor
public class MainController {

    // 서비스 주입
    private final AppleAuthService appleAuthService;

    /**
     * POST /oauth2/appleLogin
     * - 클라이언트에서 code 받아서 → 토큰 교환 + id_token 검증 → 검증된 사용자 정보 반환
     */
    @PostMapping("/oauth2/appleLogin")
    public ResponseEntity<?> appleLogin(@RequestParam("code") String code) {
        try {
            // 검증된 사용자 정보 (sub, email 등)
            JWTClaimsSet claims = appleAuthService.login(code);
            return ResponseEntity.ok(claims);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("❌ Apple login failed: " + e.getMessage());
        }
    }
}