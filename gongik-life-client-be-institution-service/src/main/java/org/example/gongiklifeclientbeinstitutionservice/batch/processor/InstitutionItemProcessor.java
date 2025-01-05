package org.example.gongiklifeclientbeinstitutionservice.batch.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.gongiklifeclientbeinstitutionservice.dto.InstitutionWithDiseaseRestrictionsDto;
import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionCategory;
import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionTag;
import org.example.gongiklifeclientbeinstitutionservice.entity.RegionalMilitaryOffice;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionCategoryRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionTagRepository;
import org.example.gongiklifeclientbeinstitutionservice.repository.RegionalMilitaryOfficeRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstitutionItemProcessor implements
    ItemProcessor<String[], InstitutionWithDiseaseRestrictionsDto> {

  private final RegionalMilitaryOfficeRepository regionalMilitaryOfficeRepository;
  private final InstitutionCategoryRepository institutionCategoryRepository;
  private final InstitutionTagRepository institutionTagRepository;

  @Override
  public InstitutionWithDiseaseRestrictionsDto process(String[] row) {
    String region = row[0];
    String name = row[1];
    String parentInstitution = row[2];
    String address = row[3];
    String regionalMilitaryOfficeCode = mapRegionalMilitaryOfficeToEnglish(row[4]);
    String phoneNumber = row[5];
    String diseaseRestriction = row[6];
    boolean sexualCriminalRecordRestriction = "y".equalsIgnoreCase(row[7]);
    String tag = mapTagToEnglish(row[8]);
    String category = mapCategoryToEnglish(row[9]);

    Optional<RegionalMilitaryOffice> regionalMilitaryOffice = regionalMilitaryOfficeRepository.findByOfficeName(
        regionalMilitaryOfficeCode);
    Optional<InstitutionCategory> institutionCategory = institutionCategoryRepository.findByCategoryName(
        category);
    Optional<InstitutionTag> institutionTag = institutionTagRepository.findByTagName(tag);

    if (regionalMilitaryOffice.isPresent() && institutionCategory.isPresent()) {
      Institution.InstitutionBuilder institutionBuilder = Institution.builder()
          .region(region)
          .name(name)
          .parentInstitution(parentInstitution)
          .address(address)
          .phoneNumber(phoneNumber)
          .regionalMilitaryOffice(regionalMilitaryOffice.get())
          .sexualCriminalRecordRestriction(sexualCriminalRecordRestriction)
          .institutionCategory(institutionCategory.get());

      institutionTag.ifPresent(institutionBuilder::tag);

      Institution institution = institutionBuilder.build();

      List<String> diseaseRestrictions = new ArrayList<>();
      if (diseaseRestriction != null && !diseaseRestriction.isEmpty()) {
        String[] diseaseNames = diseaseRestriction.split("/");
        for (String diseaseName : diseaseNames) {
          diseaseRestrictions.add(mapDiseaseToEnglish(diseaseName));
        }
      }

      return new InstitutionWithDiseaseRestrictionsDto(institution, diseaseRestrictions);
    } else {
      return null; // 필요한 데이터가 없으면 null 반환
    }
  }

  private String mapCategoryToEnglish(String category) {
    switch (category) {
      case "사회복지시설":
        return "SOCIAL_WELFARE";
      case "공공단체":
        return "PUBLIC_ORGANIZATION";
      case "국가기관":
        return "NATIONAL_AGENCY";
      case "지방자치단체":
        return "LOCAL_GOVERNMENT";
      default:
        return category;
    }
  }

  private String mapTagToEnglish(String tag) {
    switch (tag) {
      case "노인복지시설":
        return "ELDERLY_WELFARE_FACILITY";
      case "아동복지시설":
        return "CHILD_WELFARE_FACILITY";
      case "장애인복지시설":
        return "DISABLED_WELFARE_FACILITY";
      case "복지관":
        return "WELFARE_CENTER";
      case "지역주민시설":
        return "LOCAL_RESIDENT_FACILITY";
      case "보육시설":
        return "DAYCARE_CENTER";
      case "자활시설":
        return "SELF_SUFFICIENCY_CENTER";
      case "정신보건시설":
        return "MENTAL_HEALTH_FACILITY";
      case "여성복지시설":
        return "WOMEN_WELFARE_FACILITY";
      case "청소년복지시설":
        return "YOUTH_WELFARE_FACILITY";
      case "노숙인복지시설":
        return "HOMELESS_WELFARE_FACILITY";
      case "결핵한센시설":
        return "TUBERCULOSIS_HANSEN_FACILITY";
      default:
        return tag;
    }
  }

  private String mapRegionalMilitaryOfficeToEnglish(String officeName) {
    switch (officeName) {
      case "서울":
        return "SEOUL";
      case "부산.울산":
        return "BUSAN_ULSAN";
      case "대구.경북":
        return "DAEGU_GYEONGBUK";
      case "경인":
        return "GYEONGIN";
      case "광주.전남":
        return "GWANGJU_JEONNAM";
      case "대전.충남":
        return "DAEJEON_CHUNGNAM";
      case "강원":
        return "GANGWON";
      case "충북":
        return "CHUNGBUK";
      case "전북":
        return "JEONBUK";
      case "경남":
        return "GYEONGNAM";
      case "제주":
        return "JEJU";
      case "인천":
        return "INCHEON";
      case "경기북부":
        return "GYEONGGI_NORTH";
      case "강원영동":
        return "GANGWON_YEONGDONG";
      default:
        return officeName;
    }
  }

  private String mapDiseaseToEnglish(String diseaseName) {
    switch (diseaseName) {
      case "정신과질환":
        return "MENTAL_ILLNESS";
      case "경련성":
        return "SEIZURE";
      case "문신자해":
        return "TATTOO_SELF_HARM";
      case "척추질환":
        return "SPINE_DISEASE";
      case "기관지천식":
        return "BRONCHIAL_ASTHMA";
      case "아토피피부염":
        return "ATOPIC_DERMATITIS";
      default:
        return diseaseName;
    }
  }
}