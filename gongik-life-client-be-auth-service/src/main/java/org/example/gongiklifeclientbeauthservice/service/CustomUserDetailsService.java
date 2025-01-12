package org.example.gongiklifeclientbeauthservice.service;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.FindByEmailForAuthRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.FindByEmailForAuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeauthservice.model.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    FindByEmailForAuthResponse userResponse;
    try {
      userResponse = userServiceStub.findByEmailForAuth(
          FindByEmailForAuthRequest.newBuilder()
              .setEmail(email)
              .build()
      );
    } catch (Exception e) {
      log.error("failed grpc findByEmailForAuth", e);
      throw e;

    }

    if (userResponse == null || userResponse.getEmail().isEmpty()) {
      throw new UsernameNotFoundException("User not found with email: " + email);
    }

    // UserResponse에 필요한 모든 사용자 정보를 포함
    return CustomUserDetails.builder()
        .id(userResponse.getId())
        .email(userResponse.getEmail())
        .password(userResponse.getPassword())
        .name(userResponse.getName())
        .bio(userResponse.getBio().isEmpty() ? null : userResponse.getBio())
        .enlistment_date(
            userResponse.getEnlistmentDate().isEmpty() ? null : userResponse.getEnlistmentDate())
        .discharge_date(
            userResponse.getDischargeDate().isEmpty() ? null : userResponse.getDischargeDate())
        .institution(userResponse.hasInstitution() ? CustomUserDetails.InstitutionForAuth.builder()
            .id(userResponse.getInstitution().getId())
            .name(userResponse.getInstitution().getName())
            .build() : null)
        .build();


  }
}
