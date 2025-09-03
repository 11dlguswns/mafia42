package click.mafia42.util;

import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapperUtil {
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T readValueOrThrow(Object instance, Class<T> clazz) {
        try {
            if (instance instanceof String json) {
                return objectMapper.readValue(json, clazz);
            }
            String json = objectMapper.writeValueAsString(instance);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new GlobalException(GlobalExceptionCode.MALFORMED_JSON);
        }
    }
}
