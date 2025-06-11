package com.alkemy.mleon.prodmgmt.service;

import com.alkemy.mleon.prodmgmt.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO user);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(String id, UserDTO user);
    void  deleteUser(String id);

}