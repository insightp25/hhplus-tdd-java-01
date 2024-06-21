package io.hhplus.tdd.medium_e2e;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hhplus.tdd.controller.PointController;
import io.hhplus.tdd.controller.request.UserPointUpdate;
import io.hhplus.tdd.domain.TransactionType;
import io.hhplus.tdd.infrastructure.PointHistoryTable;
import io.hhplus.tdd.infrastructure.UserPointTable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/**
 * PointController 클래스에 대한 E2E 테스트입니다.
 * 각 테스트 테스트 메서드에 대한 주석 내용은 테스트 메서드 이름으로 대신하였습니다.
 * 테스트 메서드간 ApplicationContext 의 싱글톤 빈에 접근해 상태를 변경하므로 @DirtiesContext 로
 * 각 테스트 메서드마다 context cache 를 삭제하도록 해 독립적인 환경을 만들었습니다.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerTest {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;
    private final PointController pointController;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public PointControllerTest(PointHistoryTable pointHistoryTable, UserPointTable userPointTable,
        PointController pointController, MockMvc mockMvc, ObjectMapper objectMapper) {

        this.pointHistoryTable = pointHistoryTable;
        this.userPointTable = userPointTable;
        this.pointController = pointController;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @DirtiesContext
    public void 특정_유저의_포인트_정보를_조회할_수_있다() throws Exception {
        // given
        userPointTable.insertOrUpdate(7L, 500);

        // when
        // then
        mockMvc.perform(get("/point/7"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7L))
            .andExpect(jsonPath("$.point").value(500L));
    }

    @Test
    @DirtiesContext
    public void 포인트_충전한_적이_없는_유저의_포인트_정보를_조회할_시_포인트가_0_인_포인트_정보를_반환한다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/point/7"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7L))
            .andExpect(jsonPath("$.point").value(0L));
    }

    @Test
    @DirtiesContext
    public void 특정_유저의_포인트_히스토리를_조회할_수_있다() throws Exception {
        // given
        pointHistoryTable.insert(7L, 500L, TransactionType.CHARGE, 12_345L);
        pointHistoryTable.insert(7L, 300L, TransactionType.USE, 67_890L);

        // when
        // then
        mockMvc.perform(get("/point/7/histories"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").exists())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].userId").value(7L))
            .andExpect(jsonPath("$[0].amount").value(500L))
            .andExpect(jsonPath("$[0].type").value("CHARGE"))
            .andExpect(jsonPath("$[0].updateMillis").value(12_345L))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].userId").value(7L))
            .andExpect(jsonPath("$[1].amount").value(300L))
            .andExpect(jsonPath("$[1].type").value("USE"))
            .andExpect(jsonPath("$[1].updateMillis").value(67_890L));
    }

    @Test
    @DirtiesContext
    public void 특정_유저의_포인트_충전_내역이_없을_시_빈_내역을_반환한다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(get("/point/7/histories"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DirtiesContext
    public void 특정_유저의_포인트를_충전할_시_합산후_포인트_정산_정보를_반환한다() throws Exception {
        // given
        userPointTable.insertOrUpdate(7L, 1_000L);

        // when
        // then
        mockMvc.perform(patch("/point/7/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UserPointUpdate.builder()
                    .amount(2_000L)
                    .build())))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7L))
            .andExpect(jsonPath("$.point").value(1_000L + 2_000L));
    }

    @Test
    @DirtiesContext
    public void 특정_유저의_포인트를_충전할_시_충전내역이_히스토리에_기록된다() throws Exception {
        // given
        // when
        pointController.charge(7L, UserPointUpdate.builder()
            .amount(1_000L)
            .build());

        //then
        mockMvc.perform(get("/point/7/histories"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").exists())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].userId").value(7L))
            .andExpect(jsonPath("$[0].amount").value(1_000L))
            .andExpect(jsonPath("$[0].type").value("CHARGE"));
    }

    @Test
    @DirtiesContext
    public void 특정_유저의_포인트를_사용할_시_포인트_차감후_포인트_정산_정보를_반환한다() throws Exception {
        // given
        userPointTable.insertOrUpdate(7L, 5_000L);
        UserPointUpdate userPointUpdate = UserPointUpdate.builder()
            .amount(2_000L)
            .build();

        // when
        // then
        mockMvc.perform(patch("/point/7/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPointUpdate)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7L))
            .andExpect(jsonPath("$.point").value(5_000L - 2_000L));
    }

    @Test
    @DirtiesContext
    public void 특정_유저의_포인트를_사용할_시_사용내역이_히스토리에_기록된다() throws Exception {
        // given
        pointController.charge(7L, UserPointUpdate.builder()
            .amount(5_000L)
            .build());

        // when
        pointController.use(7L, UserPointUpdate.builder()
            .amount(2_000L)
            .build());

        //then
        mockMvc.perform(get("/point/7/histories"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").exists())
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].userId").value(7L))
            .andExpect(jsonPath("$[0].amount").value(5_000L))
            .andExpect(jsonPath("$[0].type").value("CHARGE"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].userId").value(7L))
            .andExpect(jsonPath("$[1].amount").value(2_000L))
            .andExpect(jsonPath("$[1].type").value("USE"));
    }

    @Test
    @DirtiesContext
    public void 포인트_충전시_0_이하의_정수를_충전포인트_값으로_입력할_경우_에러를_던진다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(patch("/point/7/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UserPointUpdate.builder()
                    .amount(-500L)
                    .build())))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.message").value("잘못된 포인트 입력입니다. 입력은 양의 정수로만 가능합니다."));
    }

    @Test
    @DirtiesContext
    public void 포인트_사용시_0_이하의_정수를_사용포인트_값으로_입력할_경우_에러를_던진다() throws Exception {
        // given
        // when
        // then
        mockMvc.perform(patch("/point/7/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UserPointUpdate.builder()
                    .amount(-500L)
                    .build())))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("400"))
            .andExpect(jsonPath("$.message").value("잘못된 포인트 입력입니다. 입력은 양의 정수로만 가능합니다."));
    }

    @Test
    @DirtiesContext
    public void 특정_유저가_보유한_포인트보다_많은_포인트를_사용하려_할_경우_에러를_반환한다() throws Exception {
        // given
        userPointTable.insertOrUpdate(7L, 1_000L);

        // when
        // then
        mockMvc.perform(patch("/point/7/use")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UserPointUpdate.builder()
                    .amount(1_500L)
                    .build())))
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("409"))
            .andExpect(jsonPath("$.message").value("포인트 잔액이 부족합니다."));
    }
}

