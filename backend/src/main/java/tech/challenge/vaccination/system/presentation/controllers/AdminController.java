package tech.challenge.vaccination.system.presentation.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.challenge.vaccination.system.application.services.UserApplicationService;
import tech.challenge.vaccination.system.domain.usecases.admin.DeleteUserUseCase;
import tech.challenge.vaccination.system.domain.valueobjects.UserId;
import tech.challenge.vaccination.system.presentation.dtos.user.PageResponseDTO;
import tech.challenge.vaccination.system.presentation.dtos.user.UpdateUserDTO;
import tech.challenge.vaccination.system.presentation.dtos.user.UserResponseDTO;
import tech.challenge.vaccination.system.presentation.mappers.UserDtoMapper;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserApplicationService userService;
    private final DeleteUserUseCase deleteUserUseCase;

    public AdminController(UserApplicationService userService, DeleteUserUseCase deleteUserUseCase) {
        this.userService = userService;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @GetMapping("/users")
    public ResponseEntity<PageResponseDTO<UserResponseDTO>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var users = userService.listUsers(page, size).stream()
                .map(UserDtoMapper::toResponseDto)
                .toList();
        long total = userService.countUsers();
        return ResponseEntity.ok(PageResponseDTO.of(users, page, size, total));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(UserDtoMapper.toResponseDto(userService.findUser(UserId.of(id))));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO dto) {
        var user = userService.updateUser(UserId.of(id), dto.name(), dto.email());
        return ResponseEntity.ok(UserDtoMapper.toResponseDto(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUserUseCase.execute(UserId.of(id));
        return ResponseEntity.noContent().build();
    }
}
