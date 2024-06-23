package io.hhplus.tdd.mock;

import io.hhplus.tdd.infrastructure.PointHistoryTable;
import io.hhplus.tdd.infrastructure.UserPointTable;
import io.hhplus.tdd.controller.PointController;
import io.hhplus.tdd.service.LockHandler;
import io.hhplus.tdd.service.PointService;
import io.hhplus.tdd.service.PointValidator;
import lombok.Builder;

/**
 * 단위 테스트용 모의 컨테이너를 생성했습니다.
 * PointHistoryTable 과 UserPointTable 자체가 DB 계층의 Stub 이라고 생각하여,
 * 컨테이너에서 Stub 으로서 그대로 사용하였습니다.
 */
public class TestContainer {

    public final PointHistoryTable pointHistoryTable;
    public final UserPointTable userPointTable;
    public final PointService pointService;
    public final PointController pointController;
    public final PointValidator pointValidator;
    public final LockHandler lockHandler;

    @Builder
    public TestContainer() {
        this.pointHistoryTable = new PointHistoryTable();
        this.userPointTable = new UserPointTable();
        this.pointValidator = new PointValidator();
        this.lockHandler = new LockHandler();
        this.pointService = PointService.builder()
            .userPointTable(this.userPointTable)
            .pointHistoryTable(this.pointHistoryTable)
            .pointValidator(this.pointValidator)
            .lockHandler(this.lockHandler)
            .build();
        this.pointController = new PointController(this.pointService);
    }
}
