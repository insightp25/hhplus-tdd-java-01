package io.hhplus.tdd.service;

import io.hhplus.tdd.controller.PointController;
import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.infrastructure.PointHistoryTable;
import io.hhplus.tdd.infrastructure.UserPointTable;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class PointService {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;
    private final PointValidator pointValidator;
    private final LockHandler lockHandler;

    private final Lock lock = new ReentrantLock();

    public UserPoint getByUserId(long userId) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> getHistoriesByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint charge(long userId, long amount) {
        return lockHandler.executeOnLock(userId, () -> {
            pointValidator.validatePointGreaterThanZero(amount);

            UserPoint userPoint = userPointTable.selectById(userId);
            userPoint = userPointTable.insertOrUpdate(userId, userPoint.point() + amount);

            pointHistoryTable.insert(userId, amount, TransactionType.CHARGE,
                System.currentTimeMillis());

            return userPoint;
        });
    }

    public UserPoint use(long userId, long amount) {
        return lockHandler.executeOnLock(userId, () -> {
            pointValidator.validatePointGreaterThanZero(amount);

            UserPoint userPoint = userPointTable.selectById(userId);

            pointValidator.validateSufficientPoints(amount, userPoint);

            userPoint = userPointTable.insertOrUpdate(userId, userPoint.point() - amount);

            pointHistoryTable.insert(userId, amount, TransactionType.USE,
                System.currentTimeMillis());

            return userPoint;
        });
    }
}
