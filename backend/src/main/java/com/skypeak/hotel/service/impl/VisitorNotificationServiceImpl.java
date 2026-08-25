package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.dto.location.GeoLocation;
import com.skypeak.hotel.service.GeoIpService;
import com.skypeak.hotel.service.TelegramNotificationService;
import com.skypeak.hotel.service.VisitorNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service
@Slf4j
public class VisitorNotificationServiceImpl implements VisitorNotificationService {

    private static final Duration NOTIFICATION_INTERVAL =
            Duration.ofMinutes(30);
    private static final DateTimeFormatter
            DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd.MM.yyyy HH:mm:ss"
            );
    private final TelegramNotificationService telegramNotificationService;
    private final GeoIpService geoIpService;
    private final Map<String, Instant> lastNotifications =
            new ConcurrentHashMap<>();

    @Override
    public void notifyVisit(String ipAddress, String userAgent) {

        Instant now = Instant.now();

        Instant lastNotification =
                lastNotifications.get(ipAddress);

        GeoLocation location =
                geoIpService.getLocation(ipAddress);

        if (lastNotification != null &&
                Duration.between(lastNotification, now)
                        .compareTo(NOTIFICATION_INTERVAL) < 0) {

            log.debug(
                    "⏳ Повторное уведомление для IP {} пропущено",
                    ipAddress
            );

            return;
        }

        String browser = detectBrowser(userAgent);
        String operatingSystem = detectOperatingSystem(userAgent);

        String time = LocalDateTime.now()
                .format(DATE_TIME_FORMATTER);

        String message = """
                🌐 Новый посетитель SkyPeak Hotel
                
                🌍 IP: %s
                📍 Страна: %s
                🏙️ Город: %s
                
                🌐 Браузер: %s
                💻 ОС: %s
                🕐 Время: %s
                
                🖥️ User-Agent: %s
                """.formatted(
                        ipAddress,
                        location.country(),
                        location.city(),
                        browser,
                        operatingSystem,
                        time,
                        userAgent
                );

        try {
            telegramNotificationService.sendMessage(message);
            lastNotifications.put(ipAddress, now);
            log.info(
                    "📨 Уведомление о посещении отправлено. IP: {}",
                    ipAddress
            );
        } catch (Exception e) {
            log.error(
                    "❌ Не удалось отправить уведомление о посещении",
                    e
            );
        }
    }

    private String detectBrowser(String userAgent) {

        if (userAgent == null) {
            return "Неизвестен";
        }

        if (userAgent.contains("YaBrowser")) {
            return "Yandex Browser";
        }

        if (userAgent.contains("Edg/")) {
            return "Microsoft Edge";
        }

        if (userAgent.contains("Chrome/")) {
            return "Google Chrome";
        }

        if (userAgent.contains("Firefox/")) {
            return "Mozilla Firefox";
        }

        if (userAgent.contains("Safari/")) {
            return "Safari";
        }

        return "Неизвестен";
    }

    private String detectOperatingSystem(String userAgent) {

        if (userAgent == null) {
            return "Неизвестна";
        }

        if (userAgent.contains("Windows")) {
            return "Windows";
        }

        if (userAgent.contains("Linux")) {
            return "Linux";
        }

        if (userAgent.contains("Mac OS X")) {
            return "macOS";
        }

        if (userAgent.contains("Android")) {
            return "Android";
        }

        if (userAgent.contains("iPhone") ||
                userAgent.contains("iPad")) {
            return "iOS";
        }

        return "Неизвестна";
    }
}
