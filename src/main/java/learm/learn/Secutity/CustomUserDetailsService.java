package learm.learn.Secutity;

import learm.learn.Entity.User;
import learm.learn.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Block login if not verified
        if (!user.isVerified()) {
            throw new UsernameNotFoundException("Email not verified for user: " + email);
        }

        // Build Spring Security User object
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name()) // Assign role like ROLE_ADMIN / ROLE_STUDENT
                .build();
    }
}
