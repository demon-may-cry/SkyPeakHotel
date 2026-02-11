package com.skypeak.hotel.security;

import com.skypeak.hotel.entity.UserEntity;
import com.skypeak.hotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Дмитрий Ельцов
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User with email " + username + " not found"));

        return new CustomUserDetails(userEntity);
    }
}
