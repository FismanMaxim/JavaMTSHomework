package com.mipt.hsse.tech.rentservice.DTOs.Requests;

import com.mipt.hsse.tech.rentservice.Domain.ConfirmationType;
import java.util.List;

public record UpdateItemTypeRequest(
    String newTypeName, double newCost, List<ConfirmationType> newConfirmations) {}
