package click.mafia42.exception;

public class GlobalException extends RuntimeException {
    private GlobalExceptionCode code;

    public GlobalExceptionCode getCode() {
        return code;
    }

    public String getCodeAndMessage() {
        return this.getMessage();
    }

    public static String getCodeAndMessage(GlobalExceptionCode code) {
        return "[ " + code.name() + " ] : " + code.getMessage();
    }

    public GlobalException(GlobalExceptionCode code) {
        super(getCodeAndMessage(code));
        this.code = code;
    }
    public GlobalException(GlobalExceptionCode code, Exception e) {
        super(getCodeAndMessage(code), e);
        this.code = code;
    }
    public GlobalException(GlobalExceptionCode code, String detailMassage) {
        super(getCodeAndMessage(code) + " | detailMassage : " + detailMassage);
        this.code = code;
    }
    public GlobalException(GlobalExceptionCode code, String detailMassage, Exception e) {
        super(getCodeAndMessage(code) + " | detailMassage : " + detailMassage, e);
        this.code = code;
    }
}
