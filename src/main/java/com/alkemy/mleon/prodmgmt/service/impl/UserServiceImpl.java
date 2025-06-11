package com.alkemy.mleon.prodmgmt.service.impl;

import com.alkemy.mleon.prodmgmt.dto.UserDTO;
import com.alkemy.mleon.prodmgmt.mapper.UserMapper;
import com.alkemy.mleon.prodmgmt.model.User;
import com.alkemy.mleon.prodmgmt.repository.UserRepository;
import com.alkemy.mleon.prodmgmt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(UserDTO user) {
        User newUser = userMapper.toEntity(user);
        User savedUser = userRepository.save(newUser);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return  userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(String id, UserDTO user) {
        return null;
    }

    @Override
    public void deleteUser(String id) {

    }
}