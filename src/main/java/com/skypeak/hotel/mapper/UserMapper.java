package com.skypeak.hotel.mapper;

import com.skypeak.hotel.dto.user.UserResponse;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.mapper.config.CentralMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования между UserEntity и UserResponse DTO.
 * <p>
 * Отвечает за конвертацию полей сущности пользователя в Data Transfer Object
 * для передачи клиентам через REST API. Использует MapStruct для автоматической
 * генерации реализации.
 * </p>
 *
 * <h3>Особенности маппинга:</h3>
 * <ul>
 *   <li>Поле {@code role} маппится из {@code role.name} сущности</li>
 *   <li>Использует центральную конфигурацию {@link CentralMapperConfig}</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see UserEntity
 * @see UserResponse
 * @see CentralMapperConfig
 */
@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {

    /**
     * Преобразует сущность пользователя в DTO для передачи клиенту.
     * <p>
     * Маппит все поля сущности, включая вложенное поле роли.
     * </p>
     *
     * @param userEntity сущность пользователя из базы данных
     * @return DTO пользователя для отправки клиенту
     */
    @BeanMapping(ignoreUnmappedSourceProperties = {
            "admin",
            "manager",
            "user",
            "active",
            "blocked",
            "password"
    })
    @Mapping(source = "role.name", target = "role")
    UserResponse toDto(UserEntity userEntity);
}
