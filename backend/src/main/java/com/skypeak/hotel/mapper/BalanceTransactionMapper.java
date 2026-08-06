package com.skypeak.hotel.mapper;

import com.skypeak.hotel.dto.balance.TransactionResponse;
import com.skypeak.hotel.entity.BalanceTransactionEntity;
import com.skypeak.hotel.mapper.config.CentralMapperConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования между BalanceTransactionEntity и TransactionResponse DTO.
 * <p>
 * Отвечает за конвертацию полей сущности транзакции баланса в Data Transfer Object
 * для передачи клиентам через REST API. Использует MapStruct для автоматической
 * генерации реализации.
 * </p>
 *
 * <h3>Особенности маппинга:</h3>
 * <ul>
 *   <li>Прямое маппинг всех полей сущности в DTO</li>
 *   <li>Использует центральную конфигурацию {@link CentralMapperConfig}</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see BalanceTransactionEntity
 * @see TransactionResponse
 * @see CentralMapperConfig
 */
@Mapper(config = CentralMapperConfig.class)
public interface BalanceTransactionMapper {

    /**
     * Преобразует сущность транзакции баланса в DTO для передачи клиенту.
     * <p>
     * Маппит все поля сущности транзакции в соответствующие поля DTO.
     * </p>
     *
     * @param entity сущность транзакции баланса из базы данных
     * @return DTO транзакции для отправки клиенту
     */
    @BeanMapping(ignoreUnmappedSourceProperties = {
            "deposit",
            "withdraw",
            "sign",
            "formattedDescription",
            "displayAmount",
            "user"
    })
    @Mapping(source = "type", target = "type")
    TransactionResponse toDto(BalanceTransactionEntity entity);
}
