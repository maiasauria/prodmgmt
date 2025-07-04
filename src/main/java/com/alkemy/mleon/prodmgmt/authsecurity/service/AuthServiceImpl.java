package com.alkemy.mleon.prodmgmt.authsecurity.service;

import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthRequest;
import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthResponse;
import com.alkemy.mleon.prodmgmt.dto.UserDto;
import com.alkemy.mleon.prodmgmt.mapper.UserMapper;
import com.alkemy.mleon.prodmgmt.model.User;
import com.alkemy.mleon.prodmgmt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j // Lombok annotation for logger
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository; // Repository to interact with User data
    private final UserMapper userMapper; // Mapper to convert between UserDTO and User entities
    private final PasswordEncoder passwordEncoder; // Encoder to hash passwords
    private final JwtService jwtService; // Service to handle JWT token generation and validation
    private final AuthenticationManager authenticationManager; // Manager to handle authentication logic

    /* * Registra un nuevo usuario en el sistema.
     *
     * @param request DTO que contiene los datos del usuario a registrar
     * @return AuthResponse que contiene el token JWT del usuario registrado
     */

@Override
public AuthResponse register(UserDto request) {
    log.info("AuthServiceImpl:register| Intentando registrar nuevo usuario: {}", request.getUsername());

    // Verificar si el usuario ya existe
    if (userRepository.existsUserByUsername(request.getUsername())) {
        log.warn("El nombre de usuario {} ya existe", request.getUsername());
        throw new BadCredentialsException("El nombre de usuario ya está en uso");
    }

    // Mapear UserDTO a User y codificar la contraseña
    User user = userMapper.toEntity(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));

    // Guardar el nuevo usuario en el repositorio
    User savedUser = userRepository.save(user);
    log.info("Nuevo usuario registrado con ID: {}", savedUser.getId());

    // Generar un token JWT para el usuario registrado
    String jwtToken = jwtService.generateToken(user);
    return AuthResponse.builder()
            .token(jwtToken)
            .message("Usuario registrado exitosamente")
            .build();
}

    /**
     * Autentica un usuario con sus credenciales.
     *
     * @param request DTO que contiene el nombre de usuario y la contraseña
     * @return AuthResponse que contiene el token JWT del usuario autenticado
     * @throws BadCredentialsException si las credenciales son inválidas
     */
    @Override
    public AuthResponse authenticate(AuthRequest request) {
        log.info("AuthServiceImpl:authenticate| Autenticando usuario: {}", request.getUsername());

        try {

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        log.error("Usuario no encontrado después de autenticación exitosa: {}", request.getUsername());
                        return new UsernameNotFoundException(request.getUsername());
                    });

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            log.info("Usuario {} encontrado, generando token JWT", user.getUsername());

            String jwtToken = jwtService.generateToken(user);
            log.info("Usuario {} autenticado exitosamente", user.getUsername());
            user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .forEach(authority -> log.info("User authority: {}", authority));

            return AuthResponse.builder()
                    .token(jwtToken)
                    .message("Sesion iniciada exitosamente")
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas para usuario: {}", request.getUsername());
            throw new BadCredentialsException("Credenciales inválidas");
        } catch (UsernameNotFoundException e) {
            log.error("Usuario no existe: {}", request.getUsername());
            throw new UsernameNotFoundException("Usuario no encontrado: " + request.getUsername());
        } catch (Exception e) {
            log.error("Error inesperado durante la autenticación del usuario: {}", request.getUsername(), e);
            throw new RuntimeException("Error interno del servidor");
        }
    }
}