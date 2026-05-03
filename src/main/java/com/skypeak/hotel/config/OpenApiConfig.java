package com.skypeak.hotel.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Дмитрий Ельцов
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI skyPeakHotelOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SkyPeak Hotel API")
                        .description("""
                        API для управления отелем SkyPeak
                        
                        📞 Phone: +7 (909) 436-27-26
                        
                        💬 Telegram: @DmitryEltsov
                        """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Дмитрий Ельцов")
                                .email("demonmaycry@mail.ru")
                                .url("https://github.com/demon-may-cry/SkyPeakHotel"))
                        .license(new License()
                                .name("Только для внутреннего использования (Internal Use Only)")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .description("Авторизация через JWT. Введите в поле Value токен, полученный при авторизации.")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
