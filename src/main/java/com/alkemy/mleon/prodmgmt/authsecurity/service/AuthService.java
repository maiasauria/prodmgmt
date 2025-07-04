package com.alkemy.mleon.prodmgmt.authsecurity.service;

import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthRequest;
import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthResponse;
import com.alkemy.mleon.prodmgmt.dto.UserDto;

public interface AuthService {
    AuthResponse register(UserDto request);
    AuthResponse authenticate(AuthRequest request);
}