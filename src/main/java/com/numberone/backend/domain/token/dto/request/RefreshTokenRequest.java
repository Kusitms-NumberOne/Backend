package com.numberone.backend.domain.token.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenRequest {
    @Comment("서버에서 발급받은 Refresh 토큰")
    private String token;
}
