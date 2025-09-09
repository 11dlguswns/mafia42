package click.mafia42.util;

import click.mafia42.exception.GlobalException;
import click.mafia42.exception.GlobalExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MapperUtil {
    public static final ObjectMapper objectMapper;

    static {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SimpleModule module = new JavaTimeModule()
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter))
                .addSerializer(LocalDate.class, new LocalDateSerializer(formatter))
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(formatter));
        objectMapper = new ObjectMapper().registerModule(module);
    }

    public static <T> T readValueOrThrow(Object instance, Class<T> clazz) {
        try {
            if (instance instanceof String json) {
                return objectMapper.readValue(json, clazz);
            }
            String json = objectMapper.writeValueAsString(instance);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new GlobalException(GlobalExceptionCode.MALFORMED_JSON, e);
        }
    }
}
