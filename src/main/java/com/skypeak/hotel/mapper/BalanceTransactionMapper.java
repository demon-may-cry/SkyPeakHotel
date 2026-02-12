package com.skypeak.hotel.mapper;

import com.skypeak.hotel.dto.balance.TransactionResponse;
import com.skypeak.hotel.entity.BalanceTransactionEntity;
import com.skypeak.hotel.mapper.config.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Дмитрий Ельцов
 */
@Mapper(config = CentralMapperConfig.class)
public interface BalanceTransactionMapper {

    @Mapping(source = "type", target = "type")
    TransactionResponse toDto(BalanceTransactionEntity entity);
}
