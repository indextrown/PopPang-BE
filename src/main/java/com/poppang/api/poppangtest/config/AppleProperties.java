package com.poppang.api.poppangtest.config;

// Lombok 어노테이션: getter, setter 메서드를 자동 생성
import lombok.Getter;
import lombok.Setter;

// yml/properties 파일 값을 자바 객체로 매핑해주는 어노테이션
import org.springframework.boot.context.properties.ConfigurationProperties;

// 이 클래스를 Spring Bean으로 등록하기 위한 어노테이션
import org.springframework.context.annotation.Configuration;

@Getter  // Lombok: 모든 필드에 대해 getter 메서드 자동 생성
@Setter  // Lombok: 모든 필드에 대해 setter 메서드 자동 생성
@Configuration // Spring: 이 클래스를 설정(Bean)으로 등록
@ConfigurationProperties(prefix = "apple") // Spring Boot: yml/properties 파일에서 "apple." 으로 시작하는 속성을 읽어와 자동으로 주입
public class AppleProperties {

    // Apple 로그인에 사용할 client_id (보통 앱의 Bundle ID)
    private String clientId;

    // Apple 개발자 계정 Team ID
    private String teamId;

    // .p8 키 파일의 Key ID
    private String keyId;

    // .p8 개인키 파일 경로 (JWT client_secret 생성에 사용)
    private String privateKeyPath;

    // 로그인 성공 후 돌아갈 redirect_uri
    private String redirectUri;

    // Apple 토큰 교환 API 엔드포인트 (보통 https://appleid.apple.com/auth/token)
    private String tokenUri;
}
