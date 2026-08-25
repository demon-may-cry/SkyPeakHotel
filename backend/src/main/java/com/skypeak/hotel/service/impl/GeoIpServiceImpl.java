package com.skypeak.hotel.service.impl;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.skypeak.hotel.dto.location.GeoLocation;
import com.skypeak.hotel.service.GeoIpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
@Slf4j
public class GeoIpServiceImpl implements GeoIpService {

    private final DatabaseReader databaseReader;

    public GeoIpServiceImpl(
            @Value("${geoip.database.path}")
            String databasePath
    ) throws IOException {

        File database =
                new File(databasePath);

        if (!database.exists()) {
            throw new IOException(
                    "GeoIP база не найдена по пути: "
                            + databasePath
            );
        }

        this.databaseReader =
                new DatabaseReader.Builder(database)
                        .build();

        log.info("🌍 GeoIP база успешно загружена");
    }

    @Override
    public GeoLocation getLocation(String ipAddress) {
        try {

            InetAddress ip =
                    InetAddress.getByName(ipAddress);

            CityResponse response =
                    databaseReader.city(ip);

            String country =
                    response.country()
                            .name();

            String city =
                    response.city()
                            .name();

            return new GeoLocation(
                    country != null
                            ? country
                            : "Неизвестна",

                    city != null
                            ? city
                            : "Неизвестен"
            );

        } catch (IOException | GeoIp2Exception e) {

            log.warn(
                    "⚠️ Не удалось определить геолокацию IP: {}",
                    ipAddress,
                    e
            );

            return new GeoLocation(
                    "Неизвестна",
                    "Неизвестен"
            );
        }
    }
}
