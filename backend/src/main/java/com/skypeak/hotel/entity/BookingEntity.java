package com.skypeak.hotel.entity;

import com.skypeak.hotel.entity.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.skypeak.hotel.entity.enums.BookingStatus.*;

/**
 * Сущность бронирования номера в отеле.
 * <p>
 * Представляет запись о бронировании комнаты пользователем на определенные даты.
 * Сохраняется в таблицу <code>bookings</code>.
 *
 * <p><strong>Поля и ограничения:</strong></p>
 * <ul>
 *   <li>{@link #id} - UUID, генерируется автоматически</li>
 *   <li>{@link #user} - связь с {@link UserEntity}, lazy loading</li>
 *   <li>{@link #room} - связь с {@link RoomEntity}, lazy loading</li>
 *   <li>{@link #checkInDate} - дата заезда, обязательная</li>
 *   <li>{@link #checkOutDate} - дата выезда, обязательная</li>
 *   <li>{@link #status} - {@link BookingStatus} (CREATED/CANCELLED/COMPLETED)</li>
 *   <li>{@link #createdAt} - дата создания, устанавливается автоматически</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see UserEntity
 * @see RoomEntity
 * @see BookingStatus
 */
@Getter
@Setter
@Entity
@Table(name = "bookings")
public class BookingEntity {
    /**
     * Уникальный идентификатор бронирования (UUID).
     * Генерируется автоматически базой данных.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Пользователь, который сделал бронирование.
     * Связь "многие к одному" с {@link UserEntity}. Lazy загрузка.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Комната, которая забронирована.
     * Связь "многие к одному" с {@link RoomEntity}. Lazy загрузка.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    /**
     * Дата заезда пользователя в отель.
     * Обязательное поле, не может быть null.
     */
    @NotNull
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    /**
     * Дата выезда пользователя из отеля.
     * Обязательное поле, не может быть null.
     */
    @NotNull
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @NotNull
    @Min(1)
    @Column(name = "guests_count", nullable = false)
    private Integer guestsCount;

    @NotNull
    @DecimalMin("0.01")
    @Column(
            name = "total_price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal totalPrice;

    /**
     * Статус бронирования.
     * Определяет текущее состояние бронирования (см. {@link BookingStatus}).
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status", nullable = false, length = 30)
    private BookingStatus status;

    /**
     * Дата и время создания бронирования.
     * Устанавливается автоматически при создании записи, не обновляется.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @SuppressWarnings("unused")
    public boolean isPending() {
        return status == PENDING;
    }

    /**
     * Проверяет, находится ли бронирование в статусе CANCELLED.
     *
     * @return true если бронирование отменено
     */
    @SuppressWarnings("unused")
    public boolean isCancelled() {
        return status == CANCELLED;
    }

    @SuppressWarnings("unused")
    public boolean isConfirmed() {
        return status == CONFIRMED;
    }

    @SuppressWarnings("unused")
    public boolean isCheckedOut() {
        return status == CHECKED_OUT;
    }

    /**
     * Проверяет, является ли бронирование активным в текущий момент.
     * Активным считается бронирование со статусом PENDING и датой заезда не позднее сегодняшней даты.
     *
     * @return true если бронирование активно
     */
    @SuppressWarnings("unused")
    public boolean isActive() {
        return (status == PENDING || status == CONFIRMED)
                && !checkOutDate.isBefore(LocalDate.now());
    }

    /**
     * Возвращает продолжительность пребывания в отеле.
     *
     * @return количество ночей между датой заезда и выезда
     */
    @SuppressWarnings("unused")
    public long getNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    @SuppressWarnings("unused")
    public BigDecimal getPricePerNight() {

        if (getNights() == 0) {

            return BigDecimal.ZERO;
        }

        return totalPrice.divide(
                BigDecimal.valueOf(getNights()),
                2,
                RoundingMode.HALF_UP
        );
    }

    /**
     * Проверяет, может ли пользователь отменить бронирование.
     * <p>
     * Пользователь может отменить бронирование только если оно находится
     * в статусе {@link BookingStatus#PENDING} или
     * {@link BookingStatus#CONFIRMED},
     * а дата заезда ещё не наступила.
     * </p>
     *
     * @return true если бронирование можно отменить
     */
    @SuppressWarnings("unused")
    public boolean canBeCancelledByUser() {
        return (status == PENDING || status == CONFIRMED)
                && checkInDate.isAfter(LocalDate.now());
    }
}