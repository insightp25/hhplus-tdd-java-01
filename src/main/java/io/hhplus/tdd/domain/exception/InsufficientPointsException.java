package io.hhplus.tdd.domain.exception;

import io.hhplus.tdd.controller.exception.BaseException;
import io.hhplus.tdd.controller.ErrorCode;

public class InsufficientPointsException extends BaseException {
    public InsufficientPointsException() {
        super(ErrorCode.INSUFFICIENT_POINTS);
    }
}
