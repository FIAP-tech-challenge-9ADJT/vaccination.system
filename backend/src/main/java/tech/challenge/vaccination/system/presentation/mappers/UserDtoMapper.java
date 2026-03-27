package tech.challenge.vaccination.system.presentation.mappers;

import tech.challenge.vaccination.system.domain.entities.User;
import tech.challenge.vaccination.system.presentation.dtos.user.CreateUserDTO;
import tech.challenge.vaccination.system.presentation.dtos.user.UserResponseDTO;

public class UserDtoMapper {

    public static User fromCreateDto(CreateUserDTO dto) {
        return User.create(
            dto.name(),
            dto.email(),
            dto.login(),
            dto.password()
        );
    }

    public static UserResponseDTO toResponseDto(User user) {
        return new UserResponseDTO(
            user.getId() != null ? user.getId().value() : null,
            user.getName().value(),
            user.getEmail().value(),
            user.getLogin().value(),
            user.getCpf(),
            user.getBirthDate(),
            user.getSex(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getRoles().stream()
                .map(RoleDtoMapper::toResponseDto)
                .toList()
        );
    }
}
