package click.mafia42.util;

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
            throw new RuntimeException("JSON parse error: ", e);
        }
    }
}
