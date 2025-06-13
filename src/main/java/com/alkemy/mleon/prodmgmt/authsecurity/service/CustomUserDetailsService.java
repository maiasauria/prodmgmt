package com.alkemy.mleon.prodmgmt.authsecurity.service;

import com.alkemy.mleon.prodmgmt.model.User;
import com.alkemy.mleon.prodmgmt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga un usuario por su nombre de usuario.
     *
     * @param username el nombre de usuario del usuario a cargar
     * @return UserDetails que representa al usuario cargado
     * @throws UsernameNotFoundException si no se encuentra un usuario con el nombre proporcionado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(" CustomUserDetailsService:loadUserByUsername | Intentando cargar usuario con nombre: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con nombre: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);

                });

        log.info("Usuario encontrado en la BD : {}", username);
        user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .forEach(authority -> log.info("User authority en custom user details: {}", authority));

        log.info("Detalles del usuario cargado: {}", user);
        return user;
    }
}