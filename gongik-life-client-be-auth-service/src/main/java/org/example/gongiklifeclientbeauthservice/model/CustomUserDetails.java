package org.example.gongiklifeclientbeauthservice.model;

import java.util.Collection;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {

  private String email;
  private String password;
  private String name;
  private String bio;
  private Date enlistment_date;
  private Date discharge_date;
  private InstitutionForAuth institution;

  @Builder
  @Getter
  public static class InstitutionForAuth {

    String id;
    String name;
  }

  private Collection<? extends GrantedAuthority> authorities;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
