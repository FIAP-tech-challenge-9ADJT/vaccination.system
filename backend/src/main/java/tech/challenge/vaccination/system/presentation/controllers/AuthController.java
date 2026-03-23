package tech.challenge.vaccination.system.presentation.controllers;

import jakarta.validation.Valid;
import tech.challenge.vaccination.system.application.services.AuthApplicationService;
import tech.challenge.vaccination.system.domain.valueobjects.UserId;
import tech.challenge.vaccination.system.infrastructure.persistence.entities.UserJpaEntity;
import tech.challenge.vaccination.system.infrastructure.security.TokenService;
import tech.challenge.vaccination.system.presentation.dtos.auth.LoginResponseDTO;
import tech.challenge.vaccination.system.presentation.dtos.user.ChangePasswordDTO;
import tech.challenge.vaccination.system.presentation.dtos.user.LoginUserDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthApplicationService authApplicationService, 
                         AuthenticationManager authenticationManager,
                         TokenService tokenService) {
        this.authApplicationService = authApplicationService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginUserDTO dto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
        var authentication = authenticationManager.authenticate(authenticationToken);
        String accessToken = tokenService.generateToken((UserJpaEntity) authentication.getPrincipal());
        return ResponseEntity.ok(LoginResponseDTO.of(accessToken));
    }

    @PostMapping("/update-profile")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto,
            @AuthenticationPrincipal UserJpaEntity authenticatedUser) {
    	UserId userId = UserId.of(authenticatedUser.getId());
    	authApplicationService.changePassword(userId, dto.password());
    	if (dto.name() != null || dto.email() != null) {
    	    authApplicationService.updateUserProfile(userId, dto.name(), dto.email());
    	}
        return ResponseEntity.noContent().build();
    }
}