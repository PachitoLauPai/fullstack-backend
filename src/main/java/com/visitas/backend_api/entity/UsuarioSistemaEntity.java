package com.visitas.backend_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "UsuarioSistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSistemaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    @NotFound(action = NotFoundAction.IGNORE)
    private RolEntity rol;

    @Column(name = "dni", unique = true)
    private String dni;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "estado")
    private Boolean estado = true;

    @Column(name = "firma_hash", columnDefinition = "TEXT")
    private String firmaHash;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
