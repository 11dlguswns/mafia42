package click.mafia42.exception;

public class GlobalException extends RuntimeException {
    public GlobalException(GlobalExceptionCode code) {
        super("[ " + code.name() + " ] : " + code.getMessage());
    }
    public GlobalException(GlobalExceptionCode code, String detailMassage) {
        super("[ " + code.name() + " ] : " + code.getMessage() + " | detailMassage : " + detailMassage);
    }
}
