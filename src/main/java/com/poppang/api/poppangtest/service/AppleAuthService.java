package com.poppang.api.poppangtest.service;

// === [내 프로젝트 클래스 임포트] =========================================
// Apple 관련 설정값(yml → 객체 매핑) 보관용. clientId, teamId, keyId, privateKeyPath 등.
import com.poppang.api.poppangtest.config.AppleProperties;
// Apple 서버에서 토큰 교환(/auth/token) 시 내려주는 JSON을 매핑할 DTO
import com.poppang.api.poppangtest.dto.AppleTokenResponse;
// client_secret(JWT) 생성 유틸. .p8 키로 ES256 서명된 JWT를 만들어줌.
import com.poppang.api.poppangtest.util.AppleJwtUtil;
import com.poppang.api.poppangtest.util.AppleJwtVerifier;
import com.poppang.api.poppangtest.entity.User;

// === [롬복/스프링/HTTP/유틸 임포트] ======================================
// Lombok: final 필드 기반 생성자를 자동 생성(@RequiredArgsConstructor)
import lombok.RequiredArgsConstructor;

// Spring의 HTTP 통신 구성요소들(ResponseEntity, HttpHeaders, MediaType 등)
import org.springframework.http.*;
// @Service 어노테이션 등 스프링 빈 등록 관련
import org.springframework.stereotype.Service;
// application/x-www-form-urlencoded 전송 바디를 구성하기 위한 Map 구현체
import org.springframework.util.LinkedMultiValueMap;
// 요청 파라미터 컬렉션의 인터페이스(LinkedMultiValueMap의 상위 타입)
import org.springframework.util.MultiValueMap;
// Spring에서 제공하는 간단한 동기 HTTP 클라이언트
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jwt.JWTClaimsSet;

@Service
@RequiredArgsConstructor
public class AppleAuthService {

    // application.yml의 apple.* 설정을 담은 객체
    private final AppleProperties properties;

    // HTTP 요청 전송용 클라이언트
    private final RestTemplate restTemplate = new RestTemplate();

    // UserService 주입
    private final UserService userService;

    // 토큰 교환 메서드
    private AppleTokenResponse exchange(String authorizationCode) throws Exception {
        // [1] Apple에 제출할 client_secret(JWT) 생성
        //     - iss(teamId), sub(clientId), aud, iat/exp 를 담은 JWT를 ES256으로 서명
        //     - .p8(private key)로 서명 → Apple은 등록된 공개키로 유효성 검증
        String clientSecret = AppleJwtUtil.createClientSecret(properties);

        // [2] Apple 토큰 엔드포인트에 보낼 폼 파라미터(body) 구성
        //     - Content-Type: application/x-www-form-urlencoded 로 전송해야 함
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", properties.getClientId());        // 앱 식별자(Bundle ID 또는 Service ID)
        params.add("client_secret", clientSecret);                // 방금 생성한 서명된 JWT
        params.add("code", authorizationCode);                    // 클라이언트가 전달한 인가 코드
        params.add("grant_type", "authorization_code");           // 고정값(인가코드 교환 플로우)
        params.add("redirect_uri", properties.getRedirectUri());  // 로그인 시작 시 사용한 redirect URI와 동일해야 함

        // [3] 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        // Apple 토큰 엔드포인트는 x-www-form-urlencoded 형식을 요구
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // [4] 최종 요청 엔티티(헤더 + 바디)
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // [5] Apple 토큰 엔드포인트 호출
        //     - URL: 보통 https://appleid.apple.com/auth/token (yml의 apple.token-uri에 설정)
        //     - 메서드: POST
        //     - 응답: JSON → AppleTokenResponse로 역직렬화(매핑)
        ResponseEntity<AppleTokenResponse> response = restTemplate.exchange(
                properties.getTokenUri(),    // 토큰 교환 URL
                HttpMethod.POST,             // HTTP 메서드
                request,                     // 헤더+바디
                AppleTokenResponse.class     // 응답 매핑 대상 타입
        );
        // 참고: 필요하면 response.getStatusCode()로 200 계열인지 검사 후 예외 처리 가능

        // [6] 응답 바디 추출 및 널체크
        AppleTokenResponse tokenResponse = response.getBody();
        if (tokenResponse == null) {
            // 네트워크/역직렬화 문제 등으로 바디가 비었을 때의 방어 로직
            throw new IllegalStateException("❌ Apple token exchange failed (response null)");
        }

        // [7] (선택) 디버깅 로그: 실제 서비스에선 Logger 사용 권장(System.out 대신)
        // System.out.println("✅ Apple token exchange 성공");
        // System.out.println(" - id_token: " + tokenResponse.getIdToken());         // JWT(유저 식별자 sub 포함)
        // System.out.println(" - access_token: " + tokenResponse.getAccessToken()); // Apple API 호출용
        // System.out.println(" - refresh_token: " + tokenResponse.getRefreshToken());// access_token 갱신용

        // [8] 컨트롤러/상위 로직에서 활용할 수 있도록 그대로 반환
        return tokenResponse;
    }

    /**
     * 최종 Apple 로그인 플로우
     * - 1) 인가코드로 토큰 교환
     * - 2) id_token 검증
     * - 3) 검증된 사용자 정보 반환
     */
    public User login(String authorizationCode) throws Exception {
        // 1. 토큰 교환
        AppleTokenResponse tokenResponse = exchange(authorizationCode);

        // 2. id_token 검증
        JWTClaimsSet claims = AppleJwtVerifier.verifyIdToken(
                tokenResponse.getIdToken(),
                properties.getClientId()
        );

        // 3. Apple Sub추출
        String sub = claims.getSubject();

        // 4. DB 확인
        return userService.findUserByUid(sub)
                .orElseGet(() ->
                        userService.createUser(sub, "apple") // ✅ 닉네임 기본값 or 별도 입력
                );
    }
}