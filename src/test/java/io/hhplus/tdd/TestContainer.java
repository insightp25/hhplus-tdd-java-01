package io.hhplus.tdd;

import io.hhplus.tdd.infrastructure.PointHistoryTable;
import io.hhplus.tdd.infrastructure.UserPointTable;
import io.hhplus.tdd.controller.PointController;
import io.hhplus.tdd.service.PointService;
import lombok.Builder;

public class TestContainer {

    public final PointHistoryTable pointHistoryTable;
    public final UserPointTable userPointTable;
    public final PointService pointService;
    public final PointController pointController;

    @Builder
    public TestContainer() {
        this.pointHistoryTable = new PointHistoryTable();
        this.userPointTable = new UserPointTable();
        this.pointService = new PointService(this.userPointTable, this.pointHistoryTable);
        this.pointController = new PointController(this.pointService);
    }
}
