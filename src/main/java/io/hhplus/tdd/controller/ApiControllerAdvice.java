package io.hhplus.tdd.controller;

import io.hhplus.tdd.controller.exception.BaseException;
import io.hhplus.tdd.controller.response.ErrorResponse;
import java.util.concurrent.TimeoutException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
            .status(Integer.parseInt(errorCode.getCode()))
            .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(value = TimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeoutException(TimeoutException e) {
        ErrorCode timeoutError = ErrorCode.TIMEOUT_ERROR;

        return ResponseEntity
            .status(Integer.parseInt(timeoutError.getCode()))
            .body(new ErrorResponse(timeoutError.getCode(), timeoutError.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorCode generalError = ErrorCode.GENERAL_ERROR;

        return ResponseEntity
            .status(Integer.parseInt(generalError.getCode()))
            .body(new ErrorResponse(generalError.getCode(), generalError.getMessage()));
    }
}
