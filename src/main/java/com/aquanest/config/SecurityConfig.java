//
//
//package com.aquanest.config;
//
//import java.util.List;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(
//            HttpSecurity http) throws Exception {
//
//        http
//                .cors(cors -> cors.configurationSource(
//                        corsConfigurationSource()))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                        .requestMatchers("/error").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
//                        .requestMatchers(
//                                HttpMethod.GET,
//                                "/api/categories",
//                                "/api/categories/**",
//                                "/api/products",
//                                "/api/products/**"
//                        ).permitAll()
//                        // Everything else stays blocked until
//                        // its authentication and authorization are configured.
//                        .anyRequest().denyAll()
//                )
//                .httpBasic(basic -> basic.disable())
//                .formLogin(form -> form.disable());
//
//        // CSRF protection remains enabled.
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // Only your local Angular development origin is allowed.
//        configuration.setAllowedOrigins(
//                List.of("http://localhost:4200")
//        );
//
//        configuration.setAllowedMethods(
//                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
//        );
//
//        configuration.setAllowedHeaders(
//                List.of("Content-Type", "Authorization", "X-XSRF-TOKEN")
//        );
//
//        // This initial catalogue uses public read-only requests.
//        configuration.setAllowCredentials(false);
//
//        UrlBasedCorsConfigurationSource source =
//                new UrlBasedCorsConfigurationSource();
//
//        source.registerCorsConfiguration("/api/**", configuration);
//
//        return source;
//    }
//}




//
//package com.aquanest.config;
//
//import java.util.function.Supplier;
//
//import java.util.List;
//
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
//import org.springframework.security.web.context.SecurityContextRepository;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.security.web.csrf.CsrfToken;
//import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
//import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
//import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.util.StringUtils;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(
//            PasswordEncoder passwordEncoder,
//            @Value("${app.admin.username}") String username,
//            @Value("${app.admin.password}") String rawPassword) {
//
//        if (username.isBlank() || rawPassword.length() < 16) {
//            throw new IllegalStateException(
//                    "Configure an admin username and a password of at least 16 characters."
//            );
//        }
//
//        var admin = User.withUsername(username)
//                .password(passwordEncoder.encode(rawPassword))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin);
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(
//            UserDetailsService userDetailsService,
//            PasswordEncoder passwordEncoder) {
//
//        var provider =
//                new DaoAuthenticationProvider(userDetailsService);
//
//        provider.setPasswordEncoder(passwordEncoder);
//
//        return new ProviderManager(provider);
//    }
//
//    @Bean
//    public SecurityContextRepository securityContextRepository() {
//        return new HttpSessionSecurityContextRepository();
//    }
//
//    @Bean
//    public CookieCsrfTokenRepository csrfTokenRepository(
//            @Value("${server.servlet.session.cookie.secure:false}")
//            boolean secureCookie) {
//
//        var repository =
//                CookieCsrfTokenRepository.withHttpOnlyFalse();
//
//        repository.setCookiePath("/");
//        repository.setCookieCustomizer(cookie -> cookie
//                .path("/")
//                .sameSite("Lax")
//                .secure(secureCookie));
//
//        return repository;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(
//            HttpSecurity http,
//            SecurityContextRepository securityContextRepository,
//            CookieCsrfTokenRepository csrfTokenRepository)
//            throws Exception {
//
//        http
//                .securityContext(context -> context
//                        .securityContextRepository(securityContextRepository)
//                )
//                .sessionManagement(session -> session
//                        .sessionFixation(fixation -> fixation.changeSessionId())
//                )
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(csrfTokenRepository)
//                        .csrfTokenRequestHandler(
//                                new SpaCsrfTokenRequestHandler()
//                        )
//                )
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                HttpMethod.GET,
//                                "/api/health",
//                                "/api/categories",
//                                "/api/categories/**",
//                                "/api/products",
//                                "/api/products/**",
//                                "/api/auth/csrf"
//                        ).permitAll()
//
//                        .requestMatchers(
//                                HttpMethod.POST,
//                                "/api/auth/login"
//                        ).permitAll()
//
//                        .requestMatchers(
//                                HttpMethod.GET,
//                                "/api/auth/me"
//                        ).authenticated()
//
//                        .requestMatchers(
//                                HttpMethod.POST,
//                                "/api/auth/logout"
//                        ).authenticated()
//
//                        .requestMatchers("/api/admin/**")
//                        .hasRole("ADMIN")
//
//                        .anyRequest().denyAll()
//                )
//                .exceptionHandling(exceptions -> exceptions
//                        .authenticationEntryPoint((request, response, exception) ->
//                                response.sendError(
//                                        HttpServletResponse.SC_UNAUTHORIZED
//                                )
//                        )
//                )
//                .formLogin(form -> form.disable())
//                .httpBasic(basic -> basic.disable())
//                .logout(logout -> logout
//                        .logoutUrl("/api/auth/logout")
//                        .invalidateHttpSession(true)
//                        .clearAuthentication(true)
//                        .deleteCookies("JSESSIONID")
//                        .logoutSuccessHandler((request, response, authentication) ->
//                                response.setStatus(
//                                        HttpServletResponse.SC_NO_CONTENT
//                                )
//                        )
//                );
//
//        return http.build();
//    }
//
//    /**
//     * Allows Angular to send the plain token in the X-XSRF-TOKEN header
//     * while retaining Spring Security's BREACH protection for the
//     * request attribute.
//     */
//    private static final class SpaCsrfTokenRequestHandler
//            implements CsrfTokenRequestHandler {
//
//        private final CsrfTokenRequestHandler plain =
//                new CsrfTokenRequestAttributeHandler();
//
//        private final XorCsrfTokenRequestAttributeHandler xor =
//                new XorCsrfTokenRequestAttributeHandler();
//
//        private SpaCsrfTokenRequestHandler() {
//            this.xor.setCsrfRequestAttributeName(null);
//        }
//
//        @Override
//        public void handle(
//                HttpServletRequest request,
//                HttpServletResponse response,
//                Supplier<CsrfToken> csrfToken) {
//
//            this.xor.handle(request, response, csrfToken);
//        }
//
//        @Override
//        public String resolveCsrfTokenValue(
//                HttpServletRequest request,
//                CsrfToken csrfToken) {
//
//            String headerValue =
//                    request.getHeader(csrfToken.getHeaderName());
//
//            return (StringUtils.hasText(headerValue)
//                    ? this.plain
//                    : this.xor
//            ).resolveCsrfTokenValue(request, csrfToken);
//        }
//    }
//}






package com.aquanest.config;

import java.util.List;
import java.util.function.Supplier;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username}") String username,
            @Value("${app.admin.password}") String rawPassword) {

        if (username.isBlank() || rawPassword.length() < 16) {
            throw new IllegalStateException(
                    "Configure an admin username and a password of at least 16 characters."
            );
        }

        var admin = User.withUsername(username)
                .password(passwordEncoder.encode(rawPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        var provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {

        var repository =
                CookieCsrfTokenRepository.withHttpOnlyFalse();

        repository.setCookiePath("/");
        repository.setCookieCustomizer(cookie -> cookie
                .path("/")
                .sameSite("None")
                .secure(true));

        return repository;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "https://aquanest-ui.vercel.app",
                "http://localhost:4200"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Content-Type",
                "Authorization",
                "X-XSRF-TOKEN",
                "X-XSRF-TOKEN"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository,
            CookieCsrfTokenRepository csrfTokenRepository)
            throws Exception {

        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )

                .securityContext(context -> context
                        .securityContextRepository(
                                securityContextRepository
                        )
                )

                .sessionManagement(session -> session
                        .sessionFixation(fixation ->
                                fixation.changeSessionId()
                        )
                )

                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(
                                new SpaCsrfTokenRequestHandler()
                        )
                )

//                .authorizeHttpRequests(auth -> auth
//
//                        .requestMatchers(
//                                HttpMethod.OPTIONS,
//                                "/**"
//                        ).permitAll()
//
//                        .requestMatchers(
//                                HttpMethod.GET,
//                                "/api/health",
//                                "/api/categories",
//                                "/api/categories/**",
//                                "/api/products",
//                                "/api/products/**",
//                                "/api/auth/csrf"
//                        ).permitAll()
//
//                        .requestMatchers(
//                                HttpMethod.POST,
//                                "/api/auth/login"
//                        ).permitAll()
//
//                        .requestMatchers(
//                                HttpMethod.GET,
//                                "/api/auth/me"
//                        ).authenticated()
//
//                        .requestMatchers(
//                                HttpMethod.POST,
//                                "/api/auth/logout"
//                        ).authenticated()
//
//                        .requestMatchers("/api/admin/**")
//                        .hasRole("ADMIN")
//
//                        .anyRequest().denyAll()
//                )


                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/health",
                                "/api/categories",
                                "/api/categories/**",
                                "/api/products",
                                "/api/products/**",
                                "/api/auth/csrf"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login",
                                "/api/orders/requests"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/auth/me"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/logout"
                        ).authenticated()

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .anyRequest().denyAll()
                )

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(
                                (request, response, exception) ->
                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED
                                        )
                        )
                )

                .formLogin(form -> form.disable())

                .httpBasic(basic -> basic.disable())

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(
                                (request, response, authentication) ->
                                        response.setStatus(
                                                HttpServletResponse.SC_NO_CONTENT
                                        )
                        )
                );

        return http.build();
    }

    private static final class SpaCsrfTokenRequestHandler
            implements CsrfTokenRequestHandler {

        private final CsrfTokenRequestHandler plain =
                new CsrfTokenRequestAttributeHandler();

        private final XorCsrfTokenRequestAttributeHandler xor =
                new XorCsrfTokenRequestAttributeHandler();

        private SpaCsrfTokenRequestHandler() {
            this.xor.setCsrfRequestAttributeName(null);
        }

        @Override
        public void handle(
                HttpServletRequest request,
                HttpServletResponse response,
                Supplier<CsrfToken> csrfToken) {

            this.xor.handle(request, response, csrfToken);
        }

        @Override
        public String resolveCsrfTokenValue(
                HttpServletRequest request,
                CsrfToken csrfToken) {

            String headerValue =
                    request.getHeader(csrfToken.getHeaderName());

            return (StringUtils.hasText(headerValue)
                    ? this.plain
                    : this.xor
            ).resolveCsrfTokenValue(request, csrfToken);
        }
    }
}
