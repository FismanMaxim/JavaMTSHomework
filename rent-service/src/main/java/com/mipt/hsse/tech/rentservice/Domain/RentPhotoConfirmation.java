package com.mipt.hsse.tech.rentservice.Domain;

import jakarta.persistence.Entity;

@Entity
public class RentPhotoConfirmation extends RentFinishConfirmation {
    private long photoId;
}
