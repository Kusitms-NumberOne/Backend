package com.numberone.backend.domain.token.service;

import com.numberone.backend.domain.member.repository.MemberRepository;
import com.numberone.backend.domain.member.service.MemberService;
import com.numberone.backend.domain.token.dto.request.TokenRequest;
import com.numberone.backend.domain.token.dto.response.*;
import com.numberone.backend.domain.token.entity.Token;
import com.numberone.backend.domain.token.repository.TokenRepository;
import com.numberone.backend.exception.badrequest.BadRequestTokenException;
import com.numberone.backend.properties.KakaoProperties;
import com.numberone.backend.properties.NaverProperties;
import com.numberone.backend.domain.token.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final KakaoProperties kakaoProperties;
    private final NaverProperties naverProperties;
    private final MemberService memberService;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;

    public TokenResponse loginKakao(TokenRequest tokenRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.add("Authorization", "Bearer " + tokenRequest.getToken());

        ResponseEntity<KakaoInfoResponse> response = restTemplate.exchange(kakaoProperties.getUser_api_url(), HttpMethod.GET, new HttpEntity<>(null, headers), KakaoInfoResponse.class);
        String email = response.getBody().getKakao_account().getEmail();
        return TokenResponse.of(getAccessToken(email));
    }

    public TokenResponse loginNaver(TokenRequest tokenRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + tokenRequest.getToken());

        ResponseEntity<NaverInfoResponse> response = restTemplate.exchange(naverProperties.getUser_api_url(), HttpMethod.GET, new HttpEntity<>(null, headers), NaverInfoResponse.class);
        String email = response.getBody().getResponse().getEmail();
        return TokenResponse.of(getAccessToken(email));
    }

    @Transactional
    public TokenResponse refresh(TokenRequest tokenRequest) {
        String email = jwtUtil.getEmail(tokenRequest.getToken());
        Token token = tokenRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 토큰"));
        String newToken = jwtUtil.createToken(email, 1000L * 60 * 60 * 24 * 14);
        token.updateAccessToken(newToken);
        tokenRepository.save(token);//redis의 경우 jpa와 달리 transactional을 이용해도 데이터 수정시에 명시적으로 save를 해줘야 함
        return TokenResponse.of(newToken);
    }

    private String getAccessToken(String email) {
        if (!memberRepository.existsByEmail(email))
            memberService.create(email);
        if (tokenRepository.existsById(email)) {
            Token token = tokenRepository.findById(email)
                    .orElseThrow(BadRequestTokenException::new);
            return token.getAccessToken();
        } else {
            String refreshToken = jwtUtil.createToken(email, 1000L * 60 * 60 * 24 * 14);//14일
            String accessToken = jwtUtil.createToken(email, 1000L * 60 * 30);//30분
            tokenRepository.save(Token.of(email, accessToken, refreshToken));
            return accessToken;
        }
    }
}
