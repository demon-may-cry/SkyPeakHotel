package com.skypeak.hotel.service;

/**
 * @author Дмитрий Ельцов
 */
public interface VisitorNotificationService {

    /**
     * Отправляет уведомление о новом посетителе.
     *
     * @param ipAddress IP-адрес посетителя
     * @param userAgent строка User-Agent посетителя
     */
    void notifyVisit(String ipAddress, String userAgent, String timezone);
}
