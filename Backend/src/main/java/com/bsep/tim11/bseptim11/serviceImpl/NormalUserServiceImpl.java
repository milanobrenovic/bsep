package com.bsep.tim11.bseptim11.serviceImpl;

import com.bsep.tim11.bseptim11.dto.NormalUserDTO;
import com.bsep.tim11.bseptim11.model.NormalUser;
import com.bsep.tim11.bseptim11.repository.NormalUserRepository;
import com.bsep.tim11.bseptim11.service.AuthService;
import com.bsep.tim11.bseptim11.service.NormalUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NormalUserServiceImpl implements UserDetailsService, NormalUserService {

    @Autowired
    private AuthService authService;

    @Autowired
    private NormalUserRepository normalUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void insertHardcodedNormalUserAfterStartup() {
//        Set<Authority> authorities = authService.findByName("ROLE_NORMAL_USER");
//        User user = new User();
//        user.setUsername("user");
//        user.setPassword("$2a$10$l8J.2UoFqfOwj9t7GRAtAen1/t8Sz2HfAxYT9LehVxq58wa9LihEi"); // pwd: 123
//        user.setAuthorities(authorities);
//
//        if (findByUsername(user.getUsername()) != null) {
//            return;
//        }
//
//        userRepository.save(user);
    }

    @Override
    public NormalUser findById(Long id){
        return normalUserRepository.findOneById(id);
    }
    @Override
    public NormalUser findOneByUsername(String username) {
        return normalUserRepository.findByUsername(username);
    }

    @Override
    public NormalUser getUserLogin() {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        try {
            NormalUser normalUser = normalUserRepository.findByUsername(currentUser.getName());
            if (normalUser != null) {
                return normalUser;
            }
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NormalUser normalUser = findOneByUsername(username);
        if (normalUser == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        } else {
            return normalUser;
        }
    }

    @Override
    public NormalUserDTO createNormalUser(NormalUserDTO normalUserDTO) {
        if (normalUserRepository.findByUsername(normalUserDTO.getUsername()) != null) {
            return null;
        }

        String hashedPassword = passwordEncoder.encode(normalUserDTO.getPassword());

        NormalUser newNormalUser = new NormalUser(
                normalUserDTO.getUsername(),
                hashedPassword,
                normalUserDTO.getFirstName(),
                normalUserDTO.getLastName()
        );
        newNormalUser.setAuthorities(authService.findById(2L));
        return new NormalUserDTO(normalUserRepository.save(newNormalUser));
    }

}
