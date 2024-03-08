package com.mipt.hsse.tech.rentservice.DTOs.Requests;

import com.mipt.hsse.tech.rentservice.Domain.ConfirmationType;
import java.util.List;

public record CreateItemTypeRequest(
    String name,
    double cost,
    List<ConfirmationType> confirmations,
    int maxRentTime,
    boolean hasLock) {}
