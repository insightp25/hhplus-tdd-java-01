package io.hhplus.tdd;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointController;
import lombok.Builder;

public class TestContainer {

    public final PointHistoryTable pointHistoryTable;
    public final UserPointTable userPointTable;
    public final PointController pointController;

    @Builder
    public TestContainer() {
        this.pointHistoryTable = new PointHistoryTable();
        this.userPointTable = new UserPointTable();
        this.pointController = new PointController(this.userPointTable, this.pointHistoryTable);
    }
}
