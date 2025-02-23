package org.example.gongiklifeclientbegraphql.dto.institution.institution;


import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.InstitutionRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionRequestDto {

  @NotBlank
  private String institutionId;


  public InstitutionRequest toProto() {
    return InstitutionRequest.newBuilder()
        .setInstitutionId(this.institutionId)
        .build();
  }
}
