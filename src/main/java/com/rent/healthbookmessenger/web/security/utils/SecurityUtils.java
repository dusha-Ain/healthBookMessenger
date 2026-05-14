package com.rent.healthbookmessenger.web.security.utils;

import com.rent.healthbookmessenger.web.security.dto.CustomProfileDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static CustomProfileDetails getCurrentProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            throw new RuntimeException("User not authenticated");
        }
        return (CustomProfileDetails) auth.getPrincipal();
    }

    public static UUID getCurrentProfileId() {
        return getCurrentProfile().getId();
    }

    public static String getCurrentProfileRole() {
        return getCurrentProfile().getRole();
    }

    public static String getCurrentProfileEmail() {
        return getCurrentProfile().getUsername();
    }

    public static boolean hasRole(String role) {
        return getCurrentProfileRole().equals(role);
    }

    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public static boolean isConsultant() {
        return hasRole("CONSULTANT");
    }

    public static boolean isDoctor() {
        return hasRole("DOCTOR");
    }

    public static boolean isUser() {
        return hasRole("USER");
    }
}