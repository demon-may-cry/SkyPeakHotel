package com.skypeak.hotel.service;

/**
 * @author Дмитрий Ельцов
 */
public interface TelegramNotificationService {

    /**
     * Отправляет уведомление в Telegram.
     *
     * @param message текст сообщения
     */
    void sendMessage(String message);
}
