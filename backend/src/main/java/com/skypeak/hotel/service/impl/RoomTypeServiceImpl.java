package com.skypeak.hotel.service.impl;

import com.skypeak.hotel.entity.RoomTypeEntity;
import com.skypeak.hotel.repository.RoomTypeEntityRepository;
import com.skypeak.hotel.service.RoomTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.text.MessageFormat.format;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RoomTypeServiceImpl implements RoomTypeService {
    
    private final RoomTypeEntityRepository roomTypeEntityRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RoomTypeEntity> getAllRoomTypes() {
        return roomTypeEntityRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomTypeEntity getBySlug(String slug) {
        return roomTypeEntityRepository.findBySlug(slug).orElseThrow(() ->
                new EntityNotFoundException(format("Тип комнаты с slug {0} не найден", slug)));
    }
}
