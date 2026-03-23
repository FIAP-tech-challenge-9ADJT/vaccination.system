package tech.challenge.vaccination.system.presentation.dtos.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDTO> fieldErrors
) {
    public static ErrorResponseDTO of(int status, String error, String message, String path) {
        return new ErrorResponseDTO(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                null
        );
    }

    public static ErrorResponseDTO of(int status, String error, String message, String path, List<FieldErrorDTO> fieldErrors) {
        return new ErrorResponseDTO(
                LocalDateTime.now(),
                status,
                error,
                message,
                path,
                fieldErrors
        );
    }
}