package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.service.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
@Slf4j
public class TelegramNotificationServiceImpl implements TelegramNotificationService {

    private final RestClient restClient;

    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.chat.id}")
    private String chatId;


    @Override
    public void sendMessage(String message) {
        String url = "https://api.telegram.org/bot"
                + botToken
                + "/sendMessage";

        restClient.post()
                .uri(url)
                .body(
                        """
                        {
                            "chat_id": "%s",
                            "text": "%s"
                        }
                        """.formatted(chatId, message)
                )
                .header("Content-Type", "application/json")
                .retrieve()
                .toBodilessEntity();
    }
}
