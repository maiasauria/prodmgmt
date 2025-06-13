package com.alkemy.mleon.prodmgmt.authsecurity.jwt;

import com.alkemy.mleon.prodmgmt.authsecurity.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer "; // Prefix for the JWT token in the Authorization header
    private final JwtService jwtService; // Servicio para manejar JWTs
    private final UserDetailsService userDetailsService; //Inyectar el servicio de detalles del usuario

    //Lista de rutas que no requieren autenticación
    private static final Set<String> WHITELISTED_PATHS = Set.of(
            "/swagger-ui", "/v3/api-docs", "/api-docs", "/favicon.ico"
    );

    /* Este méthod se ejecuta para cada solicitud entrante.
     * Verifica si la ruta es una de las rutas permitidas (whitelisted).
     * Si es así, continúa con el filtro sin aplicar autenticación.
     * Si no, extrae el token JWT del encabezado Authorization y valida la autenticación.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("JwtAuthFilter : doFilterInternal || Processing request for path: {}", request.getServletPath());

        String path = request.getServletPath();

        if (WHITELISTED_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = extractToken(request);
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String username = jwtService.extractUsername(token);
                UserDetails user = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, user)) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                log.error("JWT authentication failed", e);
                if (!response.isCommitted()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Authentication failed");
                }
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        log.info("JwtAuthFilter : extractToken || Extracting token from request headers");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (authHeader != null && authHeader.startsWith(BEARER_PREFIX))
                ? authHeader.substring(BEARER_PREFIX.length())
                : null;
    }
}