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

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    if (!user.isVerified()) {
        throw new UsernameNotFoundException("Email not verified for user: " + email);
    }

    return org.springframework.security.core.userdetails.User

            .withUsername(user.getEmail())

            .password(user.getPassword())

            .authorities(user.getRole().name()) 

            .build();
}
}
