package com.skypeak.hotel.entity;

import com.skypeak.hotel.entity.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Дмитрий Ельцов
 */
@Getter
@Setter
@Entity
@Table(name = "rooms")
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 20)
    @Column(name = "room_number", nullable = false, unique = true, length = 20)
    private String roomNumber;

    @Size(max = 30)
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "room_type", nullable = false, length = 30)
    private RoomType roomType;

    @NotNull
    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @NotNull
    @Column(name = "active", nullable = false)
    private boolean active;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

}