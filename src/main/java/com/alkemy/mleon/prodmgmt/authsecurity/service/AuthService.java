package com.alkemy.mleon.prodmgmt.authsecurity.service;

import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthRequest;
import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthResponse;
import com.alkemy.mleon.prodmgmt.dto.UserDTO;

public interface AuthService {
    AuthResponse register(UserDTO request);
    AuthResponse authenticate(AuthRequest request);
}