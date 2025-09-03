package click.mafia42.exception;

public enum GlobalExceptionCode {
    NOT_FOUND_COMMAND("존재하지 않는 커멘드입니다."),
    UNSUPPORTED_COMMAND("처리할 수 없는 커멘드입니다."),

    MALFORMED_JSON("잘못된 JSON 형식입니다."),
    INVALID_REQUEST("잘못된 요청입니다.")
    ;

    private final String message;

    GlobalExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
