package io.hhplus.tdd.small.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.domain.exception.BadInputPointValueException;
import io.hhplus.tdd.domain.exception.InsufficientPointsException;
import io.hhplus.tdd.mock.TestContainer;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

/**
 * PointController 클래스에 대한 단위 테스트입니다.
 * 각 테스트 테스트 메서드에 대한 주석 내용은 테스트 메서드 이름으로 대신하였습니다.
 */
public class PointControllerTest {

    @Test
    public void 특정_유저의_포인트_정보를_조회할_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userPointTable.insertOrUpdate(7L, 500);

        // when
        ResponseEntity<UserPoint> result = testContainer.pointController.point(7L);

        // then
        assertAll(
            () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)),
            () -> assertThat(result.getBody()).isNotNull(),
            () -> assertThat(result.getBody().id()).isEqualTo(7L),
            () -> assertThat(result.getBody().point()).isEqualTo(500L)
        );
    }

    @Test
    public void 포인트_충전한_적이_없는_유저의_포인트_정보를_조회할_시_포인트가_0_인_포인트_정보를_반환한다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        ResponseEntity<UserPoint> result = testContainer.pointController.point(7L);

        // then
        assertAll(
            () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)),
            () -> assertThat(result.getBody().id()).isEqualTo(7L),
            () -> assertThat(result.getBody().point()).isEqualTo(0L)
        );
    }

    @Test
    public void 특정_유저의_포인트_히스토리를_조회할_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.pointHistoryTable.insert(7L, 500L, TransactionType.CHARGE, 12_345L);
        testContainer.pointHistoryTable.insert(7L, 300L, TransactionType.USE, 67_890L);

        // when
        ResponseEntity<List<PointHistory>> result = testContainer.pointController.history(7L);

        // then
        assertAll(
            () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)),
            () -> assertThat(result.getBody()).isNotNull(),
            () -> assertThat(result.getBody().size()).isEqualTo(2),
            () -> assertThat(result.getBody().get(0).id()).isEqualTo(1L),
            () -> assertThat(result.getBody().get(0).userId()).isEqualTo(7L),
            () -> assertThat(result.getBody().get(0).amount()).isEqualTo(500L),
            () -> assertThat(result.getBody().get(0).type()).isEqualTo(TransactionType.CHARGE),
            () -> assertThat(result.getBody().get(0).updateMillis()).isEqualTo(12_345L),
            () -> assertThat(result.getBody().get(1).id()).isEqualTo(2L),
            () -> assertThat(result.getBody().get(1).userId()).isEqualTo(7L),
            () -> assertThat(result.getBody().get(1).amount()).isEqualTo(300L),
            () -> assertThat(result.getBody().get(1).type()).isEqualTo(TransactionType.USE),
            () -> assertThat(result.getBody().get(1).updateMillis()).isEqualTo(67_890L)
        );
    }

    @Test
    public void 특정_유저의_포인트_충전_내역이_없을_시_빈_내역을_반환한다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        ResponseEntity<List<PointHistory>> result = testContainer.pointController.history(7L);

        // then
        assertAll(
            () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)),
            () -> assertThat(result.getBody()).isEmpty()
        );
    }

    @Test
    public void 특정_유저의_포인트를_충전할_시_합산후_포인트_정산_정보를_반환한다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        testContainer.pointController.charge(7L, 500L);
        ResponseEntity<UserPoint> result = testContainer.pointController.charge(7L, 500L);

        // then
        assertAll(
            () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)),
            () -> assertThat(result.getBody()).isNotNull(),
            () -> assertThat(result.getBody().id()).isEqualTo(7L),
            () -> assertThat(result.getBody().point()).isEqualTo(1_000L)
        );
    }

    @Test
    public void 특정_유저의_포인트를_충전할_시_충전내역이_히스토리에_기록된다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        testContainer.pointController.charge(7L, 500L);
        ResponseEntity<List<PointHistory>> result = testContainer.pointController.history(7L);

        // then
        assertAll(
            () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)),
            () -> assertThat(result.getBody()).isNotNull(),
            () -> assertThat(result.getBody().size()).isEqualTo(1),
            () -> assertThat(result.getBody().get(0).id()).isEqualTo(1L),
            () -> assertThat(result.getBody().get(0).userId()).isEqualTo(7L),
            () -> assertThat(result.getBody().get(0).amount()).isEqualTo(500L),
            () -> assertThat(result.getBody().get(0).type()).isEqualTo(TransactionType.CHARGE)
        );
    }

    @Test
    public void 특정_유저의_포인트를_사용할_시_포인트_차감후_포인트_정산_정보를_반환한다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userPointTable.insertOrUpdate(7L, 1_000L);

        // when
        ResponseEntity<UserPoint> result = testContainer.pointController.use(7L, 100L);

        // then
        assertAll(
            () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)),
            () -> assertThat(result.getBody()).isNotNull(),
            () -> assertThat(result.getBody().id()).isEqualTo(7L),
            () -> assertThat(result.getBody().point()).isEqualTo(1_000L - 100L)
        );
    }

    @Test
    public void 특정_유저의_포인트를_사용할_시_사용내역이_히스토리에_기록된다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userPointTable.insertOrUpdate(7L, 1_000L);
        testContainer.pointHistoryTable.insert(7L, 1_000L, TransactionType.CHARGE, 12_345L);

        // when
        testContainer.pointController.use(7L, 300L);
        ResponseEntity<List<PointHistory>> result = testContainer.pointController.history(7L);

        // then
        assertAll(
            () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200)),
            () -> assertThat(result.getBody()).isNotNull(),
            () -> assertThat(result.getBody().size()).isEqualTo(2),
            () -> assertThat(result.getBody().get(1).id()).isEqualTo(2L),
            () -> assertThat(result.getBody().get(1).userId()).isEqualTo(7L),
            () -> assertThat(result.getBody().get(1).amount()).isEqualTo(300L),
            () -> assertThat(result.getBody().get(1).type()).isEqualTo(TransactionType.USE)
        );
    }

    @Test
    public void 포인트_충전시_0_이하의_정수를_충전포인트_값으로_입력할_경우_에러를_던진다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.pointController.charge(7L, -500L);
        }).isInstanceOf(BadInputPointValueException.class);
    }

    @Test
    public void 포인트_사용시_0_이하의_정수를_사용포인트_값으로_입력할_경우_에러를_던진다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.pointController.use(7L, -500L);
        }).isInstanceOf(BadInputPointValueException.class);
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
