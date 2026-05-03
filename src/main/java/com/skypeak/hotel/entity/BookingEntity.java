package com.skypeak.hotel.entity;

import com.skypeak.hotel.entity.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

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
    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Проверяет, находится ли бронирование в статусе CREATED.
     *
     * @return true если бронирование создано и ожидает выполнения
     */
    @SuppressWarnings("unused")
    public boolean isCreated() {
        return status == CREATED;
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

    /**
     * Проверяет, находится ли бронирование в статусе COMPLETED.
     *
     * @return true если проживание завершено
     */
    @SuppressWarnings("unused")
    public boolean isCompleted() {
        return status == COMPLETED;
    }

    /**
     * Проверяет, является ли бронирование активным в текущий момент.
     * Активным считается бронирование со статусом CREATED и датой заезда не позднее сегодняшней даты.
     *
     * @return true если бронирование активно
     */
    @SuppressWarnings("unused")
    public boolean isActive() {
        return isCreated() && !checkInDate.isAfter(LocalDate.now());
    }

    /**
     * Возвращает продолжительность пребывания в днях.
     *
     * @return количество дней между датой заезда и выезда
     */
    @SuppressWarnings("unused")
    public long getDuration() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    /**
     * Проверяет, может ли пользователь отменить это бронирование.
     * Бронирование можно отменить только если оно в статусе CREATED и дата заезда не наступила.
     *
     * @return true если бронирование можно отменить
     */
    @SuppressWarnings("unused")
    public boolean canBeCancelledByUser() {
        return isCreated() && checkInDate.isAfter(LocalDate.now());
    }

}