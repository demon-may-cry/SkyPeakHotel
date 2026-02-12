package com.skypeak.hotel.service;

import com.skypeak.hotel.dto.auth.RegisterRequest;

/**
 * @author Дмитрий Ельцов
 */
public interface RegistrationService {

    void register(RegisterRequest request);
}
