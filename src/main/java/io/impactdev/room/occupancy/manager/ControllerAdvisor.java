package io.impactdev.room.occupancy.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.google.common.base.MoreObjects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(e, serialize(ErrorResponse.from(e)), createJsonHttpHeaders(), status, request);
    }

    private String serialize(ErrorResponse response) {
        try {
            String responseJson = objectMapper.writeValueAsString(response);
            logger.debug("Error response:\n" + responseJson);
            return responseJson;
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    private HttpHeaders createJsonHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public record ErrorResponse(List<String> errors,
                                Map<String, String> fieldErrors) {

        private static ErrorResponse from(MethodArgumentNotValidException exception) {
            List<String> errors = exception.getGlobalErrors()
                    .stream()
                    .map(ObjectError::toString)
                    .toList();
            Map<String, String> fieldErrors = exception.getFieldErrors()
                    .stream()
                    .collect(Collectors.groupingBy(error -> toSnakeCase(error.getField())))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue()
                                    .stream()
                                    .map(error -> MoreObjects.firstNonNull(error.getDefaultMessage(), "Invalid value"))
                                    .sorted()
                                    .collect(Collectors.joining(". "))));
            return new ErrorResponse(errors, fieldErrors);
        }

        private static String toSnakeCase(String camelCaseName) {
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelCaseName);
        }
    }
}
