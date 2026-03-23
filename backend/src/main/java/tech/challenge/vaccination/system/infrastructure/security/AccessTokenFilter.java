package tech.challenge.vaccination.system.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;
import tech.challenge.vaccination.system.infrastructure.persistence.repositories.UserJpaRepository;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AccessTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserJpaRepository userRepository;

    private static final List<String> PUBLIC_URLS = List.of(
            "/v3/api-docs",
            "/v3/api-docs/",
            "/v3/api-docs/swagger-config",
            "/swagger-ui.html",
            "/swagger-ui/",
            "/swagger-ui/index.html",
            "/swagger-resources",
            "/swagger-resources/",
            "/webjars/");

    public AccessTokenFilter(TokenService tokenService, UserJpaRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublicUrl(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = recoveryRequestToken(request);

        if (token != null) {
            try {
                String login = tokenService.verifyToken(token);
                UserJpaEntity user = userRepository.findByLogin(login)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                        user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicUrl(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::startsWith);
    }

    private String recoveryRequestToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}