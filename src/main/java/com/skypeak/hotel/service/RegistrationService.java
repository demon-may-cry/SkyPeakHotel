package com.skypeak.hotel.service;

import com.skypeak.hotel.dto.auth.RegisterRequest;

/**
 * Сервис регистрации пользователей отеля.
 * <p>
 * Определяет контракт для создания новой учетной записи пользователя.
 * Реализации этого сервиса должны гарантировать атомарность операции:
 * пользователь и его начальный баланс создаются в рамках одной транзакции.
 *
 * <p><strong>Гарантии контракта:</strong></p>
 * <ul>
 *     <li>Уникальность email.</li>
 *     <li>Присвоение роли "USER" по умолчанию.</li>
 *     <li>Хранение пароля в зашифрованном виде (например, BCrypt).</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see RegisterRequest
 * @see com.skypeak.hotel.service.impl.RegistrationServiceImpl
 */
public interface RegistrationService {

    /**
     * Регистрирует нового пользователя в системе.
     * <p>
     * Метод создает нового пользователя на основе предоставленных данных,
     * присваивает ему роль {@code USER} и создает начальный нулевой баланс.
     *
     * @param request объект с данными для регистрации, содержащий email и пароль.
     * @throws IllegalStateException если пользователь с таким email уже существует,
     *                               или если системная роль 'USER' не найдена в базе данных.
     */
    void register(RegisterRequest request);
}
