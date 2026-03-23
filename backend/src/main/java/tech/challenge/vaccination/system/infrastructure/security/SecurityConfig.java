package tech.challenge.vaccination.system.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AccessTokenFilter accessTokenFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(AccessTokenFilter accessTokenFilter, UserDetailsService userDetailsService) {
        this.accessTokenFilter = accessTokenFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(req -> {

                // ENDPOINTS PÚBLICOS
                req.requestMatchers("/auth/login").permitAll();
                req.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/swagger-resources/**", "/webjars/**").permitAll();

                // CRIAÇÃO DE USUÁRIOS (público)
                req.requestMatchers(HttpMethod.POST, "/users").permitAll();

                // AUTH - atualizar perfil (requer autenticação)
                req.requestMatchers(HttpMethod.POST, "/auth/update-profile").hasAnyRole("USER", "ADMIN");

                // USERS
                req.requestMatchers(HttpMethod.GET, "/users/me").hasAnyRole("USER", "ADMIN");
                req.requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN");
                req.requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN");

                // ADMIN
                req.requestMatchers("/admin/**").hasRole("ADMIN");

                // QUALQUER OUTRO REQUER AUTENTICAÇÃO
                req.anyRequest().authenticated();
            })
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(accessTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
