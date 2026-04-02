package bg.uni.fmi.theatre.web;

import bg.uni.fmi.theatre.dto.ErrorResponse;
import bg.uni.fmi.theatre.exception.NotFoundException;
import bg.uni.fmi.theatre.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {















//    @ExceptionHandler(NotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ErrorResponse handleNotFound(NotFoundException ex, HttpServletRequest request) {
//        return new ErrorResponse(404, ex.getMessage(), request.getRequestURI());
//    }
//
//    @ExceptionHandler(ValidationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleValidation(ValidationException ex, HttpServletRequest request) {
//        return new ErrorResponse(400, ex.getMessage(), request.getRequestURI());
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleBindValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
//        String message = ex.getBindingResult().getFieldErrors().stream()
//                .map(FieldError::getDefaultMessage)
//                .collect(Collectors.joining("; "));
//        return new ErrorResponse(400, message, request.getRequestURI());
//    }
//
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleGeneral(Exception ex, HttpServletRequest request) {
//        return new ErrorResponse(500, "Internal server error", request.getRequestURI());
//    }
}
