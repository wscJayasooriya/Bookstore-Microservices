package com.practical.identityService.service;

import com.practical.identityService.model.Role;
import com.practical.identityService.model.User;
import com.practical.identityService.repository.RoleRepository;
import com.practical.identityService.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @PostConstruct
    public void init() {
        if (!roleRepository.findByName("ADMIN").isPresent()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            roleRepository.save(adminRole);
        }

        if (!roleRepository.findByName("USER").isPresent()) {
            Role userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }

        if (!userRepository.findByEmail("admin@example.com").isPresent()) {
            User adminUser = new User();
            adminUser.setEmail("admin@example.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setName("Admin User");
            adminUser.getRoles().add(roleRepository.findByName("ADMIN").get());
            userRepository.save(adminUser);
        }
    }
}