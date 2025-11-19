package com.example.market.service;

import com.example.market.model.User;
import com.example.market.repository.UserRepository;
import com.example.market.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Временно уберем метод регистрации чтобы скомпилировать
    // public User registerNewUser(UserRegistrationDto registrationDto) { ... }

    // Преобразование User в UserDto
    public UserDto convertToDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    // Получение всех пользователей как DTO
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Получение пользователя по ID как DTO
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return convertToDto(user);
    }
}