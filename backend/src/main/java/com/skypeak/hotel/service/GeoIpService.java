package com.skypeak.hotel.service;

import com.skypeak.hotel.dto.location.GeoLocation;

/**
 * @author Дмитрий Ельцов
 */
public interface GeoIpService {

    GeoLocation getLocation(String ipAddress);
}
