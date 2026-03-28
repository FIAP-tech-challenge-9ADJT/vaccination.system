package tech.challenge.vaccination.system.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(req -> {

                // ENDPOINTS PÚBLICOS
                req.requestMatchers("/auth/login").permitAll();
                req.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
                                "/swagger-resources/**", "/webjars/**").permitAll();

                // CRIAÇÃO DE USUÁRIOS (público)
                req.requestMatchers(HttpMethod.POST, "/users").permitAll();

                // QR Code validation (público)
                req.requestMatchers(HttpMethod.GET, "/patients/*/vaccination-card/validate").permitAll();

                // AUTH - atualizar perfil (requer autenticação)
                req.requestMatchers(HttpMethod.POST, "/auth/update-profile").hasAnyRole("USER", "ADMIN", "PACIENTE", "ENFERMEIRO", "MEDICO", "EMPRESA");

                // USERS
                req.requestMatchers(HttpMethod.GET, "/users/me").hasAnyRole("USER", "ADMIN", "PACIENTE", "ENFERMEIRO", "MEDICO", "EMPRESA");
                req.requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN");
                req.requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN");

                // VACCINES - leitura para todos autenticados, escrita para ADMIN
                req.requestMatchers(HttpMethod.GET, "/vaccines/**").authenticated();
                req.requestMatchers(HttpMethod.POST, "/vaccines").hasRole("ADMIN");
                req.requestMatchers(HttpMethod.PUT, "/vaccines/**").hasRole("ADMIN");
                req.requestMatchers(HttpMethod.DELETE, "/vaccines/**").hasRole("ADMIN");

                // HEALTH UNITS - leitura para todos autenticados, escrita para ADMIN
                req.requestMatchers(HttpMethod.GET, "/health-units/**").authenticated();
                req.requestMatchers(HttpMethod.POST, "/health-units").hasRole("ADMIN");
                req.requestMatchers(HttpMethod.PUT, "/health-units/**").hasRole("ADMIN");
                req.requestMatchers(HttpMethod.DELETE, "/health-units/**").hasRole("ADMIN");

                // VACCINATION RECORDS - registro para ENFERMEIRO/MEDICO, edição só MEDICO
                req.requestMatchers(HttpMethod.POST, "/vaccinations").hasAnyRole("ENFERMEIRO", "MEDICO");
                req.requestMatchers(HttpMethod.PUT, "/vaccinations/**").hasRole("MEDICO");
                req.requestMatchers(HttpMethod.GET, "/vaccinations/**").hasAnyRole("ADMIN", "ENFERMEIRO", "MEDICO");

                // PATIENT - carteira de vacinação (USER tratado como PACIENTE para retrocompatibilidade)
                req.requestMatchers(HttpMethod.GET, "/patients/search").hasAnyRole("ENFERMEIRO", "MEDICO", "ADMIN");
                req.requestMatchers("/patients/me/**").hasAnyRole("PACIENTE", "USER");
                req.requestMatchers(HttpMethod.GET, "/patients/*/vaccination-card").hasAnyRole("ENFERMEIRO", "MEDICO", "ADMIN");
                req.requestMatchers("/patients/*/consents/**").hasAnyRole("PACIENTE", "USER");

                // COMPANY - consulta status vacinal
                req.requestMatchers("/company/**").hasRole("EMPRESA");

                // CAMPAIGNS - leitura para todos autenticados, escrita para ADMIN
                req.requestMatchers(HttpMethod.GET, "/campaigns/**").authenticated();
                req.requestMatchers(HttpMethod.POST, "/campaigns").hasRole("ADMIN");
                req.requestMatchers(HttpMethod.PUT, "/campaigns/**").hasRole("ADMIN");
                req.requestMatchers(HttpMethod.DELETE, "/campaigns/**").hasRole("ADMIN");

                // DASHBOARD
                req.requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "ENFERMEIRO", "MEDICO");

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
