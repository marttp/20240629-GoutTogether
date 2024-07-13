package dev.tpcoder.goutbackend.common.config;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import dev.tpcoder.goutbackend.common.enumeration.RoleEnum;
import dev.tpcoder.goutbackend.common.model.RSAKeyProperties;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String privateKeyBase64;
    private final String publicKeyBase64;

    public SecurityConfig(
            @Value("${oauth.private-key}") String privateKeyBase64,
            @Value("${oauth.public-key}") String publicKeyBase64) {
        this.privateKeyBase64 = privateKeyBase64;
        this.publicKeyBase64 = publicKeyBase64;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Alternative solution, you can set via @PreAuthorize("hasRole('?')")
        // or @PreAuthorize("hasRole('?') and hasRole('?')") if many roles need to
        // handle
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Actuator
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/metrics").permitAll()
                        // Auth
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/auth/refresh").permitAll()
                        // Tour Company
                        .requestMatchers(HttpMethod.POST, "/api/v1/tour-companies").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/tour-companies/{id:\\d+}/approve")
                        .hasRole(RoleEnum.ADMIN.name())
                        // Tour
                        .requestMatchers(HttpMethod.GET, "/api/v1/tours").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tours/{id:\\d+}").permitAll()
                        .requestMatchers("/api/v1/tours").hasRole(RoleEnum.COMPANY.name())
                        // User
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                        .requestMatchers("/api/v1/users/**").hasRole(RoleEnum.ADMIN.name())
                        // User self-managed
                        .requestMatchers("/api/v1/me").hasRole(RoleEnum.CONSUMER.name())
                        // Administrator purpose
                        .requestMatchers("/api/v1/admin/**").hasRole(RoleEnum.ADMIN.name())
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtConverter;
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setPasswordEncoder(passwordEncoder);
        daoProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(daoProvider);
    }

    @Bean
    public InMemoryUserDetailsManager userDetailService() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAKeyProperties rsaInstance) {
        JWK jwk = new RSAKey.Builder(rsaInstance.publicKey()).privateKey(rsaInstance.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKeyProperties rsaInstance) {
        return NimbusJwtDecoder.withPublicKey(rsaInstance.publicKey()).build();
    }

    @Bean
    public RSAKeyProperties rsaInstance() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        // Resource privateKeyPkcs8 = resourceLoader.getResource("classPath:private_key_pkcs8.pem");
        String privateKeyContent = new String(Base64.getDecoder().decode(privateKeyBase64).clone());
        // Resource publicKey = resourceLoader.getResource("classPath:public_key.pem");
        String publicKeyContent = new String(Base64.getDecoder().decode(publicKeyBase64).clone());

        privateKeyContent = privateKeyContent.replaceAll("\\n", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "");
        publicKeyContent = publicKeyContent.replaceAll("\\n", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "");

        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
        PrivateKey privKey = kf.generatePrivate(keySpecPKCS8);
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
        return new RSAKeyProperties(pubKey, privKey);
    }
}
