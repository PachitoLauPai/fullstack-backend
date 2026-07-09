package com.visitas.backend_api.dto;

import com.visitas.backend_api.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String email;
    private String nombres;
    private String apellidos;
    private Rol rol;
    private Integer idDocente;
    private Integer idResponsable;
}
