package com.skypeak.hotel.controller;

import com.skypeak.hotel.dto.analytics.VisitRequest;
import com.skypeak.hotel.service.VisitorNotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Дмитрий Ельцов
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorNotificationService visitorNotificationService;

    @PostMapping("/visit")
    public ResponseEntity<Void> registerVisit(
            HttpServletRequest request,
            @RequestBody VisitRequest visitRequest
            ) {

        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        visitorNotificationService.notifyVisit(
                ipAddress,
                userAgent,
                visitRequest.timezone()
        );

        return ResponseEntity.ok().build();
    }

    private String getClientIp(HttpServletRequest request) {

        String xForwardedFor =
                request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null &&
                !xForwardedFor.isBlank()) {

            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp =
                request.getHeader("X-Real-IP");

        if (xRealIp != null &&
                !xRealIp.isBlank()) {

            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}

