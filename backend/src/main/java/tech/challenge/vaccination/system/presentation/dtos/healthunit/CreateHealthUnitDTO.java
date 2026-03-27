package tech.challenge.vaccination.system.presentation.dtos.healthunit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateHealthUnitDTO(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    String name,

    @Size(max = 20, message = "CNES deve ter no máximo 20 caracteres")
    String cnes,

    @Size(max = 300, message = "Endereço deve ter no máximo 300 caracteres")
    String address,

    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    String city,

    @Size(max = 2, message = "Estado deve ter 2 caracteres")
    String state,

    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    String phone
) {}
