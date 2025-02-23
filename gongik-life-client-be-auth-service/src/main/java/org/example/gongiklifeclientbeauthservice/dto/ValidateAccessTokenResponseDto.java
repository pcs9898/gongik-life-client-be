package org.example.gongiklifeclientbeauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ValidateAccessTokenResponseDto {

  @Schema(description = "User ID", example = "0c7d4cfb-563c-47bc-975e-8fa15116e0fb", required = true)
  private String userId;

}
