package io.hhplus.tdd.service;

import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.domain.exception.BadInputPointValueException;
import io.hhplus.tdd.domain.exception.InsufficientPointsException;
import org.springframework.stereotype.Component;

@Component
public class PointValidator {
    public void validatePointGreaterThanZero(long amount) {
        if (amount <= 0) {
            throw new BadInputPointValueException();
        }
    }

    public void validateSufficientPoints(long amount, UserPoint userPoint) {
        if (userPoint.point() < amount) {
            throw new InsufficientPointsException();
        }
    }
}
