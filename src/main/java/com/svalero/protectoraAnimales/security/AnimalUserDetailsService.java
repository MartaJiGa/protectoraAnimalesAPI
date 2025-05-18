package com.svalero.protectoraAnimales.security;

import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.domain.dto.user.UserOutDTO;
import com.svalero.protectoraAnimales.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AnimalUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserOutDTO user = userService.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("Invalid username/password");

        //List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
        List<GrantedAuthority> authorities = getUserAuthority(Set.of());
        return buildUserForAuthentication(user, authorities);
    }

    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        //userRoles.forEach(role -> roles.add(new SimpleGrantedAuthority(role.getName())));
        return new ArrayList<>(roles);
    }

    private UserDetails buildUserForAuthentication(UserOutDTO user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.isActive(), true, true, true, authorities);
    }
}