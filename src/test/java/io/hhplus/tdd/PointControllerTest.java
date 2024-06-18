package io.hhplus.tdd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.hhplus.tdd.point.InsufficientPointsException;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * controller에 대한 최소한의 단위 테스트 작성하였습니다.
 */
public class PointControllerTest {

    @Test
    public void 특정_유저의_포인트_정보를_조회할_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userPointTable.insertOrUpdate(7L, 500);

        // when
        UserPoint result = testContainer.pointController.point(7L);

        // then
        assertAll(
            () -> assertThat(result.id()).isEqualTo(7L),
            () -> assertThat(result.point()).isEqualTo(500L)
        );
    }

    @Test
    public void 포인트_충전한_적이_없는_유저의_포인트_정보를_조회할_시_포인트가_0_인_포인트_정보를_반환한다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        UserPoint result = testContainer.pointController.point(7L);

        // then
        assertAll(
            () -> assertThat(result.id()).isEqualTo(7L),
            () -> assertThat(result.point()).isEqualTo(0L)
        );
    }

    @Test
    public void 특정_유저의_포인트_충전_또는_이용_내역을_조회할_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.pointHistoryTable.insert(7L, 500L, TransactionType.CHARGE, 12345L);
        testContainer.pointHistoryTable.insert(7L, 300L, TransactionType.USE, 67890L);

        // when
        List<PointHistory> result = testContainer.pointController.history(7L);

        // then
        assertAll(
            () -> assertThat(result.size()).isEqualTo(2),
            () -> assertThat(result.get(0).id()).isEqualTo(1L),
            () -> assertThat(result.get(0).userId()).isEqualTo(7L),
            () -> assertThat(result.get(0).amount()).isEqualTo(500L),
            () -> assertThat(result.get(0).type()).isEqualTo(TransactionType.CHARGE),
            () -> assertThat(result.get(0).updateMillis()).isEqualTo(12345L),
            () -> assertThat(result.get(1).id()).isEqualTo(2L),
            () -> assertThat(result.get(1).userId()).isEqualTo(7L),
            () -> assertThat(result.get(1).amount()).isEqualTo(300L),
            () -> assertThat(result.get(1).type()).isEqualTo(TransactionType.USE),
            () -> assertThat(result.get(1).updateMillis()).isEqualTo(67890L)
        );
    }

    @Test
    public void 특정_유저의_포인트_충전_내역이_없을_시_빈_리스트를_반환한다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        List<PointHistory> result = testContainer.pointController.history(7L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    public void 특정_유저의_포인트를_충전할_시_합산후_포인트_정산_정보를_반환한다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        testContainer.pointController.charge(7L, 500L);
        UserPoint result = testContainer.pointController.charge(7L, 500L);

        // then
        assertAll(
            () -> assertThat(result.id()).isEqualTo(7L),
            () -> assertThat(result.point()).isEqualTo(1_000L)
        );
    }

    @Test
    public void 특정_유저의_포인트를_사용할_시_포인트_차감후_포인트_정산_정보를_반환한다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userPointTable.insertOrUpdate(7L, 1_000L);

        // when
        UserPoint result = testContainer.pointController.use(7L, 100L);

        // then
        assertAll(
            () -> assertThat(result.id()).isEqualTo(7L),
            () -> assertThat(result.point()).isEqualTo(900L)
        );
    }

    @Test
    public void 특정_유저가_보유한_포인트보다_많은_포인트를_사용하려_할_경우_에러를_반환한다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userPointTable.insertOrUpdate(7L, 200L);

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.pointController.use(7L, 500L);
        }).isInstanceOf(InsufficientPointsException.class);
    }
}
