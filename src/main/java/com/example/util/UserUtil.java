package com.example.util;

import com.example.auth.CustomUserPrincipal;
import com.example.qa.model.Auditable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserUtil {
    private Object getUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getPrincipal();
    }

    public String getUserName() {
        Object principal = getUserPrincipal();
        if (principal == null) {
            return null;
        }

        if (principal instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public String getFullName() {
        Object principal = getUserPrincipal();
        if (principal == null) {
            return null;
        }

        if (principal instanceof CustomUserPrincipal) {
            return ((CustomUserPrincipal) principal).getName();
        } else {
            return "";
        }
    }

    public boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities()
                .stream()
                //.anyMatch(auth -> auth.getAuthority().equals("ROLE" + role));
                .anyMatch(auth ->
                        java.util.Arrays.stream(roles)
                                .anyMatch(role -> auth.getAuthority().equals("ROLE_" + role))
                );
    }

    public boolean isAdmin() {
        return hasAnyRole("ADMIN");
    }

    public boolean isUser() {
        return hasAnyRole("USER");
    }

    public <T extends Auditable> boolean isCreator(T entity) {
        return entity.getCreatedBy().equals(getUserName());
    }

    public <T extends Auditable> boolean hasEditPermission(T entity) {
        return isCreator(entity) || isAdmin();
    }

    public <T extends Auditable> void checkEditPermission(T entity) {
        if (!hasEditPermission(entity)) {
            throw new AccessDeniedException("You don't have permission to edit this entity");
        }
    }

    public <T extends Auditable> boolean hasFetchPermission(T entity) {
        return hasEditPermission(entity);
    }

}
