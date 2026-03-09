package com.skypeak.hotel.mapper;

import com.skypeak.hotel.dto.user.UserResponse;
import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Дмитрий Ельцов
 */
@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {

    @Mapping(source = "roleEntity.name", target = "role")
    UserResponse toDto(UserEntity userEntity);
}
