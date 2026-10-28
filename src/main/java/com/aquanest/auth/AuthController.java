

package com.aquanest.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final CookieCsrfTokenRepository csrfTokenRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            CookieCsrfTokenRepository csrfTokenRepository) {

        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    public record LoginRequest(
            @NotBlank @Size(max = 100) String username,
            @NotBlank @Size(max = 200) String password
    ) {}

    public record AuthResponse(
            String username,
            List<String> roles
    ) {}

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-store");

        Authentication authenticated;

        try {
            authenticated = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            loginRequest.username().trim(),
                            loginRequest.password()
                    )
            );
        } catch (AuthenticationException exception) {
            SecurityContextHolder.clearContext();

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .cacheControl(CacheControl.noStore())
                    .body(Map.of(
                            "message",
                            "Invalid username or password."
                    ));
        }

        // Create a session and rotate its ID after authentication.
        request.getSession(true);
        request.changeSessionId();

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);

        // Persist authentication to the server-managed session.
        securityContextRepository.saveContext(
                context, request, response
        );

        // Obtain a fresh CSRF token after login.
        csrfTokenRepository.saveToken(null, request, response);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(toResponse(authenticated));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> currentUser(
            Authentication authentication) {

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(toResponse(authentication));
    }

    private AuthResponse toResponse(Authentication authentication) {
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList();

        return new AuthResponse(authentication.getName(), roles);
    }




}