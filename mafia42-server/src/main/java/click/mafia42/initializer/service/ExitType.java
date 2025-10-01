package click.mafia42.initializer.service;

public enum ExitType {
    SELF("님이 퇴장하셨습니다"), KICKED("님이 강제퇴장 되었습니다");

    String message;

    ExitType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
