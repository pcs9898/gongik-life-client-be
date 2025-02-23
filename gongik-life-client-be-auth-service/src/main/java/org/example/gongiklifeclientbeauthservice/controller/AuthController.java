package org.example.gongiklifeclientbeauthservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbeauthservice.dto.RefreshAccessTokenResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.ServiceSignInResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.SignInResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.SigninRequestDto;
import org.example.gongiklifeclientbeauthservice.dto.ValidateAccessTokenResponseDto;
import org.example.gongiklifeclientbeauthservice.dto.response.Response;
import org.example.gongiklifeclientbeauthservice.producer.UserLoginHistoryProducer;
import org.example.gongiklifeclientbeauthservice.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Auth API")
public class AuthController {

  private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
  private static final String BEARER_PREFIX = "Bearer ";
  private final UserLoginHistoryProducer userLoginHistoryProducer;
  private final AuthService authService;

  @Operation(
      summary = "Sign In",
      description = "Sign In with email and password",
      security = {} // 보안 설정 제외
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Sign In Success",
          content = @Content(schema = @Schema(implementation = SignInResponseDto.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Wrong email or password",
          content = @Content(schema = @Schema())
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Authentication failed",
          content = @Content(schema = @Schema())
      )
  })
  @PostMapping("/signIn")
  public Response<SignInResponseDto> signIn(
      @RequestBody @Valid SigninRequestDto signinRequest,
      HttpServletRequest request,
      HttpServletResponse response
  ) {

    return Response.success(createSignInResponse(authService.signIn(signinRequest, request,
        response)));
  }


  @Operation(
      summary = "Validate Access Token",
      description = "Validate Access Token and return userId",
      security = {@SecurityRequirement(name = "bearer-key")} // 보안 설
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Valid Access Token",
          content = @Content(schema = @Schema(implementation = ValidateAccessTokenResponseDto.class))
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Invalid Access Token",
          content = @Content(schema = @Schema())
      )
  })
  @PostMapping("/validateAccessToken")
  public Response<ValidateAccessTokenResponseDto> validateAccessToken(
      @Parameter(description = "Bearer access token", required = true)
      HttpServletRequest request) {

    String accessToken = extractBearerToken(request)
        .orElseThrow(() -> new RuntimeException("Missing or invalid Authorization header"));

    return Response.success(ValidateAccessTokenResponseDto.builder()
        .userId(authService.validateAccessToken(accessToken))
        .build());
  }

  @Operation(
      summary = "Refresh Access Token",
      description = "Refresh Access Token with refresh token from cookie, need to add refresh token to cookie when test"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Refresh Access Token Success",
          content = @Content(schema = @Schema(implementation = RefreshAccessTokenResponseDto.class))
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Invalid Refresh Token",
          content = @Content(schema = @Schema())
      )
  })
  @PostMapping("/refreshAccessToken")
  public Response<RefreshAccessTokenResponseDto> refreshAccessToken(HttpServletRequest request) {
    return Response.success(authService.refreshAccessToken(getRefreshTokenFromCookie(request)));
  }

  @Operation(
      summary = "Sign Out",
      description = "Sign Out with refresh token from cookie,  need to add refresh token to cookie when test"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Sign Out Success",
          content = @Content(schema = @Schema())
      ),
      @ApiResponse(
          responseCode = "401",
          description = "Invalid Refresh Token",
          content = @Content(schema = @Schema())
      )
  })
  @PostMapping("/signOut")
  public Response<Void> signOut(HttpServletRequest request, HttpServletResponse response) {
    authService.signOut(getRefreshTokenFromCookie(request), response);
    return Response.success();
  }


  private SignInResponseDto createSignInResponse(ServiceSignInResponseDto serviceResponse) {
    return SignInResponseDto.builder()
        .user(serviceResponse.getUser())
        .accessToken(serviceResponse.getAccessToken())
        .accessTokenExpiresAt(serviceResponse.getAccessTokenExpiresAt())
        .build();
  }


  private String getRefreshTokenFromCookie(HttpServletRequest request) {
    return Optional.ofNullable(request.getCookies())
        .flatMap(cookies -> Arrays.stream(cookies)
            .filter(c -> REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
            .map(Cookie::getValue)
            .findFirst())
        .orElseThrow(() -> new RuntimeException("refreshToken not found"));
  }


  private Optional<String> extractBearerToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader("Authorization"))
        .filter(header -> header.startsWith(BEARER_PREFIX))
        .map(header -> header.substring(BEARER_PREFIX.length()));
  }


}
