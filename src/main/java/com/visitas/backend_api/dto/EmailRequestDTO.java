package com.visitas.backend_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequestDTO {

    @Email
    @NotBlank(message = "El destinatario es requerido")
    private String destinatario;

    @NotBlank(message = "El asunto es requerido")
    private String asunto;

    @NotBlank(message = "El mensaje es requerido")
    private String mensaje;
}
