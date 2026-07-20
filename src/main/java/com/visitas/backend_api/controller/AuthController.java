package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.LoginRequestDTO;
import com.visitas.backend_api.dto.LoginResponseDTO;
import com.visitas.backend_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        System.out.println("LOGIN REQUEST - Email: [" + loginRequest.getEmail() + "]");
        System.out.println("LOGIN REQUEST - Password length: " + loginRequest.getPassword().length());
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class, AuthenticationException.class})
    public ResponseEntity<Map<String, String>> handleBadCredentials(Exception ex) {
        System.out.println("ERROR DE AUTENTICACION: " + ex.getMessage());
        ex.printStackTrace();
        Map<String, String> response = new HashMap<>();
        response.put("error", "Credenciales incorrectas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        System.out.println("ERROR GENERAL: " + ex.getClass().getName() + " - " + ex.getMessage());
        ex.printStackTrace();
        Map<String, String> response = new HashMap<>();
        response.put("error", "Error interno: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
