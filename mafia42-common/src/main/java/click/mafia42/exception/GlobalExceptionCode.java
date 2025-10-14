package click.mafia42.exception;

public enum GlobalExceptionCode {
    UNKNOWN_ERROR("알 수 없는 예외가 발생했습니다."),
    NOT_INITIALIZED_UI("UI가 아직 초기화되지 않았습니다."),

    NOT_FOUND_COMMAND("존재하지 않는 커멘드입니다."),
    UNSUPPORTED_COMMAND("처리할 수 없는 커멘드입니다."),

    MALFORMED_JSON("잘못된 JSON 형식입니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    MALFORMED_TOKEN("잘못된 JWT 형식입니다."),

    DB_CONNECTION_FAIL("데이터베이스 연결에 실패했습니다."),
    DB_DISCONNECTION_FAIL("데이터베이스 정상 종료에 실패했습니다."),
    NOT_FOUND_ENV("필수 환경변수 값이 존재하지 않습니다."),
    OUT_OF_TRANSACTION("트랜잭션 범위 밖에서 커넥션을 얻을 수 없습니다."),
    TRANSACTION_FAIL("트랜잭션 작업 도중 오류가 발생했습니다."),

    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다."),
    NOT_FOUND_REFRESH_TOKEN("리프레쉬 토큰이 존재하지 않습니다."),
    NOT_FOUND_USER("유저가 존재하지 않습니다."),
    USER_ALREADY_EXISTS("이미 존재하는 유저입니다."),
    SYNC_PROCESS_EXCEPTION("동기 작업 도중 오류가 발생했습니다."),

    ROOM_MEMBER_FULL("방 인원이 꽉 찼습니다."),
    ROOM_REMOVE_NOT_ALLOWED("방을 제거할 수 없는 상태입니다."),
    NOT_FOUND_ROOM("방을 찾을 수 없습니다."),
    NOT_JOIN_ROOM("방에 참여한 상태가 아닙니다."),
    ALREADY_JOINED_ROOM("방에 이미 참여하고 있습니다."),
    CLIENT_WRITE_FAIL("화면 출력에 실패했습니다."),
    GAME_ALREADY_STARTED("게임이 이미 시작되어 있습니다."),
    ROOM_MANAGE_NOT_ALLOWED("방 관리 권한이 없습니다."),
    CANNOT_KICK_SELF("자기 자신을 강퇴할 수 없습니다."),

    GAME_START_FAIL("게임을 시작할 수 없는 상태입니다."),
    SKILL_NOT_AVAILABLE("직업 능력이 없습니다.");

    private final String message;

    GlobalExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
