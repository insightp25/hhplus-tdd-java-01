package io.hhplus.tdd.controller;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BAD_INPUT_POINT_VALUE("400", "잘못된 포인트 입력입니다. 입력은 0보다 큰 수이어야 합니다"),
    TIMEOUT_ERROR("408", "시간이 초과되었습니다"),
    INSUFFICIENT_POINTS("409", "포인트 잔액이 부족합니다"),
    GENERAL_ERROR("500", "서버 오류가 발생했습니다");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
