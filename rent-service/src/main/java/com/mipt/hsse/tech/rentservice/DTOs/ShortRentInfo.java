package com.mipt.hsse.tech.rentservice.DTOs;

import com.mipt.hsse.tech.rentservice.Domain.Entities.Person;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public final class ShortRentInfo {
  @NotNull private final long id;
  @NotNull private final Person renter;
  @NotNull private final LocalDateTime startTime;
  @NotNull private final LocalDateTime endTime;
}
