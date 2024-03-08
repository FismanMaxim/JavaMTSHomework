package com.mipt.hsse.tech.rentservice.DTOs.Requests;

import java.time.LocalDateTime;

public record EditRentTimeRequest(
    long itemId, LocalDateTime newStartTime, LocalDateTime newEndTime) {}
