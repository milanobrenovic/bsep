package com.bsep.tim11.bseptim11.serviceImpl;

import com.bsep.tim11.bseptim11.model.Admin;
import com.bsep.tim11.bseptim11.model.Authority;
import com.bsep.tim11.bseptim11.repository.AdminRepository;
import com.bsep.tim11.bseptim11.service.AdminService;
import com.bsep.tim11.bseptim11.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AdminServiceImpl implements UserDetailsService, AdminService {

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminRepository adminRepository;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void insertHardcodedAdminAfterStartup() {
        Set<Authority> authorities = authService.findByName("ROLE_ADMIN");
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("$2a$10$l8J.2UoFqfOwj9t7GRAtAen1/t8Sz2HfAxYT9LehVxq58wa9LihEi"); // pwd: 123
        admin.setAuthorities(authorities);

        if (findByUsername(admin.getUsername()) != null) {
            return;
        }

        adminRepository.save(admin);
    }

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = findByUsername(username);
        if (admin == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        } else {
            return admin;
        }
    }
}
