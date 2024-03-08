package com.mipt.hsse.tech.rentservice.DTOs.Responses;

import com.mipt.hsse.tech.rentservice.DTOs.ShortRentInfo;

public record RentInfoResponse(
    long itemId, long itemTypeId, String displayName, ShortRentInfo rent) {}
