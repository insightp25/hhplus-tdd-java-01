package io.hhplus.tdd.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record UserPointUpdate(
    long amount
) {
    @Builder
    public UserPointUpdate(
        @JsonProperty("amount") long amount
    ){
        this.amount = amount;
    }
}
