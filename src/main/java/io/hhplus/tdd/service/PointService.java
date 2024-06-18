package io.hhplus.tdd.service;

import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.repository.PointHistoryTable;
import io.hhplus.tdd.repository.UserPointTable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * service의 메서드 네임 컨벤션
 */
@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint getByUserId(
        long userId
    ) {
        return userPointTable.selectById(userId);
    }

    public List<PointHistory> getHistoriesByUserId(
        long userId
    ) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint charge (
        long userId,
        long amount
    ) {
        pointHistoryTable.insert(userId, amount, TransactionType.CHARGE, System.currentTimeMillis());

        UserPoint userPoint = userPointTable.selectById(userId);

        return userPointTable.insertOrUpdate(userId, userPoint.point() + amount);
    }

    public UserPoint use (
        long userId,
        long amount
    ) {
        pointHistoryTable.insert(userId, amount, TransactionType.USE, System.currentTimeMillis());

        UserPoint userPoint = userPointTable.selectById(userId);

        return userPointTable.insertOrUpdate(userId, userPoint.point() - amount);
    }
}
