package com.alkemy.mleon.prodmgmt.service;

import com.alkemy.mleon.prodmgmt.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);
    List<UserDto> getAllUsers();
    UserDto updateUser(String id, UserDto user);
    void  deleteUser(String id);

}