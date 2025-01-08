package org.example.gongiklifeclientbeinstitutionservice.document;

import com.gongik.institutionService.domain.service.InstitutionServiceOuterClass.SearchInstitution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "institutions_index")
public class InstitutionDocument {

  @Id
  @Field(type = FieldType.Keyword)
  private String id;  // UUID를 String으로 저장

  @Field(type = FieldType.Text)
  private String name;

  @Field(type = FieldType.Text)
  private String address;

  @Field(type = FieldType.Double)
  private Double averageRating;

  public SearchInstitution toProto() {
    return SearchInstitution.newBuilder()
        .setId(this.id)
        .setName(this.name)
        .setAddress(this.address)
        .setAverageRating(this.averageRating != null ? this.averageRating.floatValue() : 0.0f)
        .build();
  }
}
