package com.rent.healthbookmessenger.web.security.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomProfileDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String role;

    public CustomProfileDetails(String email, String role, UUID id) {
        this.email = email;
        this.role = role;
        this.id = id;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    public UUID getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}