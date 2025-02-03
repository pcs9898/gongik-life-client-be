package org.example.gongiklifeclientbeauthservice.model;

import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.example.gongiklifeclientbeauthservice.dto.SignInUserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {


  private String id;
  private String email;
  private String password;
  private String name;
  private String bio;
  private String enlistment_date;
  private String discharge_date;
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

  public SignInUserDto toSignInUserDto() {
    return SignInUserDto.builder()
        .id(this.id) // Assuming email is used as id
        .email(this.email)
        .name(this.name)
        .bio(this.bio != null ? this.bio : "")
        .enlistmentDate(this.enlistment_date != null ? this.enlistment_date : null)
        .dischargeDate(this.discharge_date != null ? this.discharge_date : null)
        .institution(this.institution != null ? SignInUserDto.SignInstitutionDto.builder()
            .id(this.institution.getId())
            .name(this.institution.getName())
            .build() : null)

        .build();
  }
}
