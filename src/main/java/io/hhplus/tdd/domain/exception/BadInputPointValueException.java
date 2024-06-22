package io.hhplus.tdd.domain.exception;

import io.hhplus.tdd.controller.exception.BaseException;
import io.hhplus.tdd.controller.ErrorCode;

public class BadInputPointValueException extends BaseException {
    public BadInputPointValueException() {
        super(ErrorCode.BAD_INPUT_POINT_VALUE);
    }
}
