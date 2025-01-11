package org.example.gongiklifeclientbeauthservice.service;

import com.gongik.userService.domain.service.UserServiceGrpc;
import com.gongik.userService.domain.service.UserServiceOuterClass.FindByEmailForAuthRequest;
import com.gongik.userService.domain.service.UserServiceOuterClass.FindByEmailForAuthResponse;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.gongiklifeclientbeauthservice.model.CustomUserDetails;
import org.example.gongiklifeclientbeauthservice.utils.TimestampConverter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  @GrpcClient("gongik-life-client-be-user-service")
  private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    FindByEmailForAuthResponse userResponse = userServiceStub.findByEmailForAuth(
        FindByEmailForAuthRequest.newBuilder()
            .setEmail(email)
            .build()
    );

    if (userResponse == null || userResponse.getEmail().isEmpty()) {
      throw new UsernameNotFoundException("User not found with email: " + email);
    }

    // UserResponse에 필요한 모든 사용자 정보를 포함
    return CustomUserDetails.builder()
        .email(userResponse.getEmail())
        .password(userResponse.getPassword())
        .name(userResponse.getName())
        .bio(userResponse.getBio())
        .enlistment_date(
            TimestampConverter.convertStringToTimestamp(userResponse.getEnlistmentDate()))
        .discharge_date(
            TimestampConverter.convertStringToTimestamp(userResponse.getDischargeDate()))
        .institution(CustomUserDetails.InstitutionForAuth.builder()
            .id(userResponse.getInstitution().getId())
            .name(userResponse.getInstitution().getName())
            .build())
        .build();
  }
}
