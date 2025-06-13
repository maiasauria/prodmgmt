package com.alkemy.mleon.prodmgmt.authsecurity.service;


import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthRequest;
import com.alkemy.mleon.prodmgmt.authsecurity.dto.AuthResponse;
import com.alkemy.mleon.prodmgmt.dto.UserDTO;
import com.alkemy.mleon.prodmgmt.mapper.UserMapper;
import com.alkemy.mleon.prodmgmt.model.User;
import com.alkemy.mleon.prodmgmt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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
    public AuthResponse register(UserDTO request) {
        log.debug("Intentando registrar nuevo usuario: {}", request.getUsername());

        // Check if the user already exists using the custom method
        if (userRepository.existsUserByUsername(request.getUsername())) {
            log.warn("Username {} already exists", request.getUsername());
            //throw new UserAlreadyExistsException("Username already exists");
        }

        // Map the UserDTO to User entity and encode the password
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        //user.setRoles(request.getRoles());

        // Save the new user to the repository
        User savedUser = userRepository.save(user);
        log.info("Nuevo usuario registrado con ID: {}", savedUser.getId());

        // Generate a JWT token for the newly registered user
        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
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
        log.debug("Autenticando usuario: {}", request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        log.error("Usuario no encontrado después de autenticación exitosa: {}", request.getUsername());
                        return new UsernameNotFoundException("User not found");
                    });

            String jwtToken = jwtService.generateToken(user);
            log.info("Usuario {} autenticado exitosamente", user.getUsername());
            user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .forEach(authority -> log.info("User authority: {}", authority));

            return AuthResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (AuthenticationException e) {
            log.warn("Falló autenticación para usuario: {}", request.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}