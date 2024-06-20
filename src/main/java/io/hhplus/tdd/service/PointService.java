package io.hhplus.tdd.service;

import io.hhplus.tdd.controller.PointController;
import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.domain.exception.InsufficientPointsException;
import io.hhplus.tdd.domain.exception.BadInputPointValueException;
import io.hhplus.tdd.repository.PointHistoryTable;
import io.hhplus.tdd.repository.UserPointTable;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    private final Lock lock = new ReentrantLock();

    public UserPoint getByUserId(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> getHistoriesByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint charge(long userId, long amount) {
        validatePointGreaterThanZero(amount);
        UserPoint userPoint = userPointTable.selectById(userId);

        lock.lock();
        try {
            pointHistoryTable.insert(userId, amount, TransactionType.CHARGE,
                System.currentTimeMillis());

            userPoint = userPointTable.insertOrUpdate(userId, userPoint.point() + amount);
        } finally {
            lock.unlock();
        }

        return userPoint;
    }

    public UserPoint use(long userId, long amount) {
        validatePointGreaterThanZero(amount);
        UserPoint userPoint = userPointTable.selectById(userId);
        validateSufficientPoints(amount, userPoint);

        lock.lock();
        try {
            pointHistoryTable.insert(userId, amount, TransactionType.USE,
                System.currentTimeMillis());

            userPoint = userPointTable.insertOrUpdate(userId, userPoint.point() - amount);
        } finally {
            lock.unlock();
        }

        return userPoint;
    }

    private static void validatePointGreaterThanZero(long amount) {
        if (amount <= 0) {
            log.warn("BadInputPointValueException: amount={}", amount);

            throw new BadInputPointValueException();
        }
    }

    private static void validateSufficientPoints(long amount, UserPoint userPoint) {
        if (userPoint.point() < amount) {
            log.warn("InsufficientPointsException: requested amount={}, current amount={}",
                amount, userPoint.point());

            throw new InsufficientPointsException();
        }
    }
}
