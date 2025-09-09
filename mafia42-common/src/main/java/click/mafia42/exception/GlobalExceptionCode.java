package click.mafia42.exception;

public enum GlobalExceptionCode {
    NOT_FOUND_COMMAND("존재하지 않는 커멘드입니다."),
    UNSUPPORTED_COMMAND("처리할 수 없는 커멘드입니다."),

    MALFORMED_JSON("잘못된 JSON 형식입니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    MALFORMED_TOKEN("잘못된 JWT 형식입니다."),

    DB_CONNECTION_FAIL("데이터베이스 연결에 실패했습니다."),
    DB_DISCONNECTION_FAIL("데이터베이스 정상 종료에 실패했습니다."),
    NOT_FOUND_ENV("필수 환경변수 값이 존재하지 않습니다."),
    TRANSACTION_FAIL("트랜잭션 작업 도중 오류가 발생했습니다."),

    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다."),
    NOT_FOUND_REFRESH_TOKEN("리프레쉬 토큰이 존재하지 않습니다."),
    NOT_FOUND_USER("유저가 존재하지 않습니다."),
    USER_ALREADY_EXISTS("이미 존재하는 유저입니다."),
    SYNC_PROCESS_EXCEPTION("동기 작업 도중 오류가 발생했습니다.");

    private final String message;

    GlobalExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
