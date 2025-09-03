package click.mafia42.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.hibernate.validator.HibernateValidator;

import java.util.Set;

public class ValidationUtil {
    private static final Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory()
            .getValidator();

    public static <T> T validationAndGet(Object obj, Class<T> clazz) {
        T value = MapperUtil.readValueOrThrow(obj, clazz);

        validate(value);
        
        return value;
    }

    private static <T> void validate(T value) {
        Set<ConstraintViolation<T>> violations = validator.validate(value);
        if (!violations.isEmpty()) {
            ConstraintViolation<T> violation = violations.stream().findFirst().orElse(null);
            throw new RuntimeException("잘못된 요청 : " + violation);
        }
    }
}
