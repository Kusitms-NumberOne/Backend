package com.numberone.backend.exception.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomExceptionContext implements ExceptionContext {
    // MEMBER 관련 예외
    NOT_FOUND_MEMBER("존재하지 않는 회원을 조회할 수 없습니다.", 1000),

    // TOKEN 관련 예외
    WRONG_ACCESS_TOKEN("존재하지 않거나 만료된 액세스 토큰입니다. 리프레쉬 토큰을 이용하여 액세스 토큰을 갱신해주세요.", 2001),
    WRONG_REFRESH_TOKEN("존재하지 않거나 만료된 리프레쉬 토큰입니다. 다시 리프레쉬 토큰을 발급받아주세요.", 2002),
    BAD_REQUEST_SOCIAL_TOKEN("요청하신 네이버 또는 카카오 소셜 토큰이 유효하지 않습니다.", 2003),

    // SHELTER 관련 예외
    NOT_FOUND_SHELTER("주변에 가까운 대피소가 존재하지 않습니다.", 3000);


    /**
     * 2023. 10. 02. versatile0010
     * <p>
     * 이 곳에서 각 예외 별 메세지와 예외 코드를 관리합니다.
     * <p>
     * 예를 들어,
     * // MEMBER
     * NOT_FOUND_MEMBER("존재하지 않는 회원을 조회할 수 없습니다.", 2000),
     * DUPLICATED_MEMBER_NAME("회원의 이름은 중복될 수 없습니다, 2001),
     * // STATION
     * NOT_FOUND_STATION("존재하지 않는 지하철역을 조회할 수 없습니다.", 3000);
     * 과 같이 작성할 수 있습니다.
     */
    private final String message;
    private final int code;
}
