package io.hhplus.tdd.small.domain;

import io.hhplus.tdd.domain.UserPoint;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * UserPoint 도메인 모델에 대한 단위 테스트입니다.
 * 각 테스트 테스트 메서드에 대한 주석 내용은 테스트 메서드 이름으로 대신하였습니다.
 */
public class UserPointTest {

    @Test
    public void 특정_유저의_id로_포인트가_0인_정보를_생성해_반환할_수_있다() {
        // given
        // when
        UserPoint result = UserPoint.empty(7L);

        // then
        Assertions.assertThat(result.id()).isEqualTo(7L);
        Assertions.assertThat(result.point()).isEqualTo(0L);
    }
}
