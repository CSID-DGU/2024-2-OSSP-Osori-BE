package dongguk.osori.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // global
    INVALID_INPUT_VALUE(400, "잘못된 입력 값입니다."),
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),
    UNAUTHORIZED_ACCESS(401, "권한이 없습니다."),
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    FORBIDDEN_ACCESS(403, "접근 권한이 없습니다."),
    SESSION_EXPIRED(401, "세션이 만료되었습니다."),
    INVALID_REQUEST_BODY(400, "요청 본문이 잘못되었습니다."),
    MISSING_REQUIRED_FIELD(400, "필수 필드가 누락되었습니다."),
    INVALID_DATA_FORMAT(400, "유효하지 않은 데이터 형식입니다."),
    INVALID_FIELD_VALUE(422, "요청 본문의 데이터가 유효하지 않습니다."),
    FIELD_VALIDATION_FAILED(422, "필드 값 검증에 실패했습니다."),
    UNSUPPORTED_MEDIA_TYPE(415, "지원하지 않는 Content-Type입니다."),


    // user
    UNAUTHORIZED(403, "인증되지 않은 사용자입니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    SESSION_USER_NOT_FOUND(401, "세션에 userId가 없습니다."),
    COUPLE_ONLY_ACCESS(403, "커플인 경우에만 접근할 수 있습니다."),

    // policy
    POLICY_NOT_FOUND(404, "해당 정책을 찾을 수 없습니다."),
    POLICY_ALREADY_EXISTS(409, "이미 존재하는 정책입니다."),


    // comment
    COMMENT_NOT_FOUND(404, "댓글을 찾을 수 없습니다."),
    COMMENT_DELETE_FORBIDDEN(403, "댓글 삭제 권한이 없습니다."),

    // portfolio
    PORTFOLIO_NOT_FOUND(404, "포트폴리오를 찾을 수 없습니다."),
    PORTFOLIO_ACCESS_FORBIDDEN(403, "해당 포트폴리오에 접근할 권한이 없습니다.");


    private final int status;
    private final String message;
}
