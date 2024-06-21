package io.hhplus.tdd.controller;

import io.hhplus.tdd.controller.response.ErrorResponse;
import io.hhplus.tdd.domain.exception.BadInputPointValueException;
import io.hhplus.tdd.domain.exception.InsufficientPointsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }

    @ExceptionHandler(value = InsufficientPointsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientPointsException(Exception e) {
        return ResponseEntity.status(409).body(new ErrorResponse("409", "포인트 잔액이 부족합니다."));
    }

    @ExceptionHandler(value = BadInputPointValueException.class)
    public ResponseEntity<ErrorResponse> handleBadInputPointValueException(Exception e) {
        return ResponseEntity.status(400).body(new ErrorResponse("400", "잘못된 포인트 입력입니다. 입력은 양의 정수로만 가능합니다."));
    }
}
