package com.mipt.hsse.tech.rentservice.DTOs.Requests;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;

public record RentItemRequest(
    long itemId, @NotNull LocalDateTime startTime, @NotNull LocalDateTime endTime) {}
