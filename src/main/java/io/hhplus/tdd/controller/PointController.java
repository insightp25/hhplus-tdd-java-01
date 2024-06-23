package io.hhplus.tdd.controller;

import io.hhplus.tdd.controller.request.UserPointUpdate;
import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public ResponseEntity<UserPoint> point(
            @PathVariable long id
    ) {
        return ResponseEntity
            .ok()
            .body(pointService.getByUserId(id));
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointHistory>> history(
            @PathVariable long id
    ) {
        return ResponseEntity
            .ok()
            .body(pointService.getHistoriesByUserId(id));
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPoint> charge(
        @PathVariable long id,
        @RequestBody UserPointUpdate userPointUpdate
    ) {
        return ResponseEntity
            .ok()
            .body(pointService.charge(id, userPointUpdate.amount()));
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<UserPoint> use(
        @PathVariable long id,
        @RequestBody UserPointUpdate userPointUpdate
    ) {
        return ResponseEntity
            .ok()
            .body(pointService.use(id, userPointUpdate.amount()));
    }
}
