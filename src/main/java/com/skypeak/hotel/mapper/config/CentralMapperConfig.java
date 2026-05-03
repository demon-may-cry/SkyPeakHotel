package com.skypeak.hotel.mapper.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Центральная конфигурация MapStruct для всех мапперов проекта.
 * <p>
 * Определяет общие настройки для всех мапперов в приложении, обеспечивая
 * единообразие поведения и политик маппинга. Используется всеми мапперами
 * через аннотацию {@code @Mapper(config = CentralMapperConfig.class)}.
 * </p>
 *
 * <h3>Настройки конфигурации:</h3>
 * <ul>
 *   <li><strong>componentModel = "spring"</strong> - интеграция с Spring IoC контейнером</li>
 *   <li><strong>unmappedTargetPolicy = ERROR</strong> - ошибка при отсутствии маппинга для целевого поля</li>
 *   <li><strong>unmappedSourcePolicy = WARN</strong> - предупреждение при отсутствии маппинга для исходного поля</li>
 * </ul>
 *
 * <h3>Политики маппинга:</h3>
 * <ul>
 *   <li><strong>ERROR для target:</strong> гарантирует, что все поля DTO будут заполнены</li>
 *   <li><strong>WARN для source:</strong> позволяет игнорировать лишние поля Entity</li>
 *   <li><strong>Spring компоненты:</strong> мапперы регистрируются как Spring бины</li>
 * </ul>
 *
 * @author Дмитрий Ельцов
 * @see org.mapstruct.MapperConfig
 * @see org.mapstruct.ReportingPolicy
 */
@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.WARN
)
public interface CentralMapperConfig {
}
