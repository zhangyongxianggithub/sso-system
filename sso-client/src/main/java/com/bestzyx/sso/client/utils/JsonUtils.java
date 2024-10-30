package com.bestzyx.sso.client.utils;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhangyongxiang on 2021/6/10 7:55 下午
 **/
@Slf4j
public final class JsonUtils {
    
    private JsonUtils() {}
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    static {
        MAPPER.findAndRegisterModules();
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        MAPPER.configure(
                DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY,
                false);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
    }
    
    public static String toJson(final Object object) {
        if (object != null) {
            try {
                return MAPPER.writeValueAsString(object);
            } catch (final JsonProcessingException e) {
                log.error("convert object: {} to json failed", object, e);
                throw new JSONException(e);
            }
        }
        return null;
    }
    
    public static <T> T fromJson(final String json,
            final TypeReference<T> type) {
        if (StringUtils.isNoneBlank(json) && type != null) {
            try {
                return MAPPER.readValue(json, type);
            } catch (final IOException e) {
                log.error("deserialized from json {} failed", json, e);
                throw new JSONException(e);
            }
        }
        return null;
    }
    
    public static void main(final String... args) {
        System.out.println(toJson(new DurationObject()));
        System.out.println(fromJson(toJson(new DurationObject()),
                new TypeReference<DurationObject>() {}));
    }
    
    @Data
    public static class DurationObject {
        private LocalDateTime localDateTime = LocalDateTime.now();
        
        private Duration duration = Duration.ofDays(1);
    }
}
