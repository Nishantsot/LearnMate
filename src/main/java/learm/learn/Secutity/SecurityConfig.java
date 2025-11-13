package learm.learn.Secutity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // ✅ enables @PreAuthorize("hasRole('TUTOR')")
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter; // ✅ your custom JWT filter

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ Enable CORS and disable CSRF (important for React frontend)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            // ✅ Define authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public routes (register, login, verify OTP, forgot password, etc.)
                .requestMatchers(
                    "/auth/**",
                    "/error",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // 👑 Admin-only routes
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 🧑‍🏫 Tutor-only routes
                .requestMatchers("/tutor/**").hasRole("TUTOR")

                // 🎓 Student-only routes (if you add later)
                .requestMatchers("/student/**").hasRole("STUDENT")

                // All other routes must be authenticated
                .anyRequest().authenticated()
            )

            // ✅ Use JWT (stateless session)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ✅ Add JWT filter before default authentication filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            // ✅ Disable form & basic login
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // ✅ Proper CORS setup for React
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ✅ Password encoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Auth manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
