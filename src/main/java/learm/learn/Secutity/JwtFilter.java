package learm.learn.Secutity;

import learm.learn.Entity.User;
import learm.learn.Repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // =========================================
        // PUBLIC AUTH ROUTES
        // =========================================

        String path = request.getServletPath();

        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // =========================================
        // GET AUTHORIZATION HEADER
        // =========================================

        final String header =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        String token = null;
        String username = null;

        // =========================================
        // NO TOKEN
        // =========================================

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // =========================================
        // EXTRACT TOKEN
        // =========================================

        token = header.substring(7);

        // =========================================
        // VALIDATE TOKEN
        // =========================================

        try {

            if (!jwtUtil.validateToken(token)) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                response.getWriter().write(
                        "Invalid or expired JWT token"
                );

                return;
            }

            username = jwtUtil.extractUsername(token);

        } catch (Exception e) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().write(
                    "Invalid or expired JWT token"
            );

            return;
        }

        // =========================================
        // SET SECURITY CONTEXT
        // =========================================

        if (username != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            User user = userRepository
                    .findByEmail(username)
                    .orElse(null);

            if (user == null) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                response.getWriter().write(
                        "User not found"
                );

                return;
            }

            // =====================================
            // CHECK EMAIL VERIFICATION
            // =====================================

            if (!user.isVerified()) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                response.getWriter().write(
                        "User email not verified"
                );

                return;
            }

            // =====================================
            // CREATE USER DETAILS
            // =====================================

            UserDetails userDetails =
                    org.springframework.security.core.userdetails.User
                            .withUsername(user.getEmail())
                            .password(user.getPassword())
                            .authorities(
                                    "ROLE_" +
                                    user.getRole().name()
                            )
                            .build();

            // =====================================
            // CREATE AUTHENTICATION
            // =====================================

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}