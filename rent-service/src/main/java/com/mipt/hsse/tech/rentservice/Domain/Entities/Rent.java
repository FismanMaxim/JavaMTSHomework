package com.mipt.hsse.tech.rentservice.Domain.Entities;

import com.mipt.hsse.tech.rentservice.Domain.RentFinishConfirmation;
import com.mipt.hsse.tech.rentservice.Domain.RentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@Entity
public class Rent {
  @Setter(AccessLevel.NONE)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  private String orderId;

  @NotNull
  @OneToOne(optional = false)
  @JoinColumn(name = "item_id")
  private Item item;

  @NotNull
  @OneToOne(optional = false)
  @JoinColumn(name = "renter_id")
  private Person renter;

  @NotNull private RentStatus status;

  @NotNull private LocalDateTime startTime;

  @NotNull private LocalDateTime endTime;

  @NotNull private LocalDateTime factStartTime;

  @NotNull private LocalDateTime factEndTime;

  @NotNull
  @OneToMany(mappedBy = "id")
  private List<RentFinishConfirmation> confirmations;

  public boolean isEnded() {
    return status == RentStatus.ENDED;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    Rent rent = (Rent) o;
    return getId() == rent.getId();
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
