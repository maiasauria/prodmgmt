package com.alkemy.mleon.prodmgmt.authsecurity.service;

import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}") // Clave secreta para firmar el JWT
    private String secretKey;

    @Value("${app.jwt.expiration}") // Tiempo de expiración del JWT en milisegundos
    private Long expirationMs;

    private Key signingKey; // Clave para firmar el JWT, se inicializa una vez

    /**
     * Genera un token JWT para el usuario proporcionado.
     *
     * @param user Los detalles del usuario para quien se generará el token.
     * @return Un token JWT como cadena.
     */
    public String generateToken(UserDetails user) { // Genera un JWT para el usuario proporcionado
        return Jwts.builder() // Construye el JWT
                .subject(user.getUsername()) // Establece el sujeto del JWT como el nombre de usuario
                .claim("roles", user.getAuthorities().stream() // Agrega los roles del usuario como una reclamación
                        .map(auth -> auth.getAuthority()) // Mapea las autoridades a sus nombres
                        .toList())  // Convierte las autoridades a una lista de cadenas
                .issuedAt(new Date()) // Establece la fecha de emisión del JWT como la fecha actual
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // Establece la fecha de expiración del JWT sumando el tiempo de expiración al tiempo actual
                .signWith(getSigningKey(), resolveAlgorithm()) // Firma el JWT con la clave de firma y el algoritmo de firma
                .compact(); // Compacta el JWT en una cadena
    }

    /**
     * Verifica si el token es válido para el usuario proporcionado.
     *
     * @param token El token JWT a verificar.
     * @param user  Los detalles del usuario para comparar con el token.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails user) { // Verifica si el token es válido para el usuario proporcionado
        try { // Intenta analizar el token JWT
            return user.getUsername().equals(extractClaim(token, Claims::getSubject)) && // Compara el nombre de usuario del token con el del usuario
                    !extractClaim(token, Claims::getExpiration).before(new Date()); // Verifica que la fecha de expiración del token no sea anterior a la fecha actual
        } catch (JwtException e) { // Captura cualquier excepción relacionada con JWT, por ejemplo, si el token es inválido o ha expirado
            return false;
        }
    }

    /**
     * Extrae el nombre de usuario del token JWT.
     *
     * @param token El token JWT del cual se extraerá el nombre de usuario.
     * @return El nombre de usuario extraído del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // Extrae el nombre de usuario del token utilizando una función de resolución
    }

    /**
     * Extrae una reclamación específica del token JWT utilizando un resolvedor de funciones.
     *
     * @param token   El token JWT del cual se extraerá la reclamación.
     * @param resolver Una función que define cómo extraer la reclamación deseada.
     * @param <T>     El tipo de la reclamación a extraer.
     * @return El valor de la reclamación extraída.
     */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        try {
            return resolver.apply(Jwts.parser() // Analiza el token JWT
                    .setSigningKey(getSigningKey()) // Establece la clave de firma para verificar el token
                    .build() // Construye el analizador de JWT
                    .parseClaimsJws(token) // Analiza el token y obtiene las reclamaciones
                    .getBody()); // Obtiene el cuerpo de las reclamaciones del token
        } catch (ExpiredJwtException e) {
            return resolver.apply(e.getClaims()); // Si el token ha expirado, devuelve las reclamaciones del token expirado
        }
    }

    /**
     * Extrae los roles del token JWT.
     *
     * @return Un conjunto de roles extraídos del token.
     */
    private Key getSigningKey() {
        if (signingKey == null) { // Si la clave de firma aún no ha sido inicializada, la crea
            signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)); // Decodifica la clave secreta en Base64 y crea una clave HMAC para firmar el JWT
        }
        return signingKey;
    }

    /**
     * Resuelve el algoritmo de firma a utilizar para firmar el JWT.
     *
     * @return El algoritmo de firma a utilizar.
     */
    private SignatureAlgorithm resolveAlgorithm() {
        return SignatureAlgorithm.HS256; // Usa directamente HS256, que es común y compatible
    }
}
