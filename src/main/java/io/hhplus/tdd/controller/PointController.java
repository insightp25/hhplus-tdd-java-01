package io.hhplus.tdd.controller;

import io.hhplus.tdd.domain.PointHistory;
import io.hhplus.tdd.domain.UserPoint;
import io.hhplus.tdd.service.PointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody long amount
    ) {
        return ResponseEntity
            .ok()
            .body(pointService.charge(id, amount));
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<UserPoint> use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return ResponseEntity
            .ok()
            .body(pointService.use(id, amount));
    }
}
