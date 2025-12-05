package com.example.market.service;

import com.example.market.model.User;
import com.example.market.repository.UserRepository;
import com.example.market.dto.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Преобразование User в UserResponseDTO
    public UserResponseDTO convertToDTO(User user) {
        if (user == null) return null;
        return new UserResponseDTO(user);
    }

    // Получение всех пользователей как DTO
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Получение пользователя по ID как DTO
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return convertToDTO(user);
    }
}
