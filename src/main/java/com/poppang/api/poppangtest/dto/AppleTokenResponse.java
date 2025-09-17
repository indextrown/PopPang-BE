package com.poppang.api.poppangtest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppleTokenResponse {
    // JWT형태이며 내부의 sub값으로 uid 사용하면 됨
    // JwKs 공캐키로 서명확인 후 사용
    @JsonProperty("id_token")
    private String idToken;

    // Apple API 호출에 사용할 수 있는 토큰
    @JsonProperty("access_token")
    private String accessToken;

    // access_token 유효기간
    @JsonProperty("expires_in")
    private String expiresIn;

    // 만료된 access_token을 갱신할 때 사용. (유저가 revoke 하지 않는 이상 유효)
    @JsonProperty("refresh_token")
    private String refreshToken;

    // Bearer
    @JsonProperty("token_type")
    private String tokenType;
}