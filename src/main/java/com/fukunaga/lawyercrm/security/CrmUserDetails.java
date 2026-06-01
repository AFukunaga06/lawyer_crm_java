package com.fukunaga.lawyercrm.security;

import com.fukunaga.lawyercrm.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 認証済みユーザー。テナントIDを保持し、コントローラ／サービスで現在テナントの特定に使う。
 */
public class CrmUserDetails implements UserDetails {

    private final User user;

    public CrmUserDetails(User user) {
        this.user = user;
    }

    public Long getUserId() { return user.getId(); }
    public Long getTenantId() { return user.getTenantId(); }
    public String getDisplayName() { return user.getName(); }
    public boolean isSuperAdmin() { return user.isSuperAdmin(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase()));
    }

    @Override public String getPassword() { return user.getPasswordHash(); }
    @Override public String getUsername() { return user.getLoginId(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return user.isActive(); }
}
