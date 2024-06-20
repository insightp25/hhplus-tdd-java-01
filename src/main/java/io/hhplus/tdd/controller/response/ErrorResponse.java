package io.hhplus.tdd.controller.response;

public record ErrorResponse(
        String code,
        String message
) {
}
