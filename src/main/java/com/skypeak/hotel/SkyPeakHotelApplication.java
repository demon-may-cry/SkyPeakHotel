package com.skypeak.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class SkyPeakHotelApplication {

	static void main(String[] args) {
		SpringApplication.run(SkyPeakHotelApplication.class, args);
	}

}
