package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* controller의 역할 외 로직들이 많이 섞여있지만, 우선 최소한의 단위 테스트 통과를 목표로 구현하였습니다.
 * 향후 아래 내용을 추가할 계획입니다.
 * - 이후 입력값에 대한 validation 구현
 * - controller 외 로직들을 service로 빼내 역할 분리
 * - 레이어드 아키텍처로 패키지 구조 개선
 * - 레이어별 단위 테스트
 * - mockMvc를 활용한 API 테스트 추가
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log =
        LoggerFactory.getLogger(PointController.class);

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        return userPointTable.selectById(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

        UserPoint userPoint = userPointTable.selectById(id);

        return userPointTable.insertOrUpdate(id, userPoint.point() + amount);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        UserPoint userPoint = userPointTable.selectById(id);

        // 포인트 부족 예외 클래스를 추가하고, advice 에서 해당 예외를 핸들링하도록 구현하였습니다.
        if (userPoint.point() < amount) {
            log.error("포인트 부족 오류 발생: userId=" + id + ", 유저 보유 포인트=" + userPoint.point() +
                ", 사용시도 포인트=" + amount);

            throw new InsufficientPointsException();
        }

        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());

        return userPointTable.insertOrUpdate(id, userPoint.point() - amount);
    }
}
