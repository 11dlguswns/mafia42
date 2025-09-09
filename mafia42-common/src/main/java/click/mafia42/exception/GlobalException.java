package click.mafia42.exception;

public class GlobalException extends RuntimeException {
    public GlobalException(GlobalExceptionCode code) {
        super("[ " + code.name() + " ] : " + code.getMessage());
    }
    public GlobalException(GlobalExceptionCode code, Exception e) {
        super("[ " + code.name() + " ] : " + code.getMessage(), e);
    }
    public GlobalException(GlobalExceptionCode code, String detailMassage) {
        super("[ " + code.name() + " ] : " + code.getMessage() + " | detailMassage : " + detailMassage);
    }
    public GlobalException(GlobalExceptionCode code, String detailMassage, Exception e) {
        super("[ " + code.name() + " ] : " + code.getMessage() + " | detailMassage : " + detailMassage, e);
    }
}
