package com.n11.bootcamp.ecommerce.security;

import com.n11.bootcamp.ecommerce.security.jwt.KeycloakRealmRolesConverter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

/**
 * Platform-wide HTTP security defaults composed by every service's SecurityConfig.
 * <p>
 * Applies: stateless sessions, CSRF off (stateless API, no cookies), and an OAuth2
 * resource server wired with the Keycloak realm-roles converter.
 * <p>
 *
 * Usage:
 * <pre>
 * @Bean
 * SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 *     return SecurityDefaults.applyDefaults(http)
 *         .authorizeHttpRequests(auth -&gt; auth
 *             .requestMatchers(...).permitAll()
 *             .anyRequest().authenticated())
 *         .build();
 * }
 * </pre>
 */
public final class SecurityDefaults {

    private SecurityDefaults() {}

    public static HttpSecurity applyDefaults(HttpSecurity http) throws Exception {
        return http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
    }

    private static JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRolesConverter());
        return converter;
    }
}