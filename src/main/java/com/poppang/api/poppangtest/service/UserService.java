package com.poppang.api.poppangtest.service;

import com.poppang.api.poppangtest.entity.User;
import com.poppang.api.poppangtest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.PropertyResolver;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PropertyResolver propertyResolver;

    public User getUserByUsername(String uid, String provider) {
        return userRepository.findById(uid)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .id(uid)
                            .provider(provider)
                            .build();
                    return userRepository.save(newUser);
                });
    }
}