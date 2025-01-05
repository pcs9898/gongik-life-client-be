//package org.example.gongiklifeclientbeinstitutionservice.loader;
//
//import com.opencsv.exceptions.CsvException;
//import java.io.IOException;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.gongiklifeclientbeinstitutionservice.entity.Institution;
//import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionDiseaseRestriction;
//import org.example.gongiklifeclientbeinstitutionservice.entity.InstitutionDiseaseRestrictionId;
//import org.example.gongiklifeclientbeinstitutionservice.repository.DiseaseRestrictionRepository;
//import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionCategoryRepository;
//import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionDiseaseRestrictionRepository;
//import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionRepository;
//import org.example.gongiklifeclientbeinstitutionservice.repository.InstitutionTagRepository;
//import org.example.gongiklifeclientbeinstitutionservice.repository.RegionalMilitaryOfficeRepository;
//import org.example.gongiklifeclientbeinstitutionservice.util.CsvUtil;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class DataLoader implements CommandLineRunner {  18분 41초 소요 -너무 느려서 안씀
//
//  private final CsvUtil csvUtil;
//  private final InstitutionRepository institutionRepository;
//  private final RegionalMilitaryOfficeRepository regionalMilitaryOfficeRepository;
//  private final DiseaseRestrictionRepository diseaseRestrictionRepository;
//  private final InstitutionTagRepository institutionTagRepository;
//  private final InstitutionCategoryRepository institutionCategoryRepository;
//  private final InstitutionDiseaseRestrictionRepository institutionDiseaseRestrictionRepository;
//
//  @Override
//  public void run(String... args) throws Exception {
//    log.info("DataLoader run method started");
//
//    if (institutionRepository.existsAny()) {
//      log.info("Data already exists, skipping data loading.");
//      return;
//    }
//
//    long startTime = System.currentTimeMillis();
//    loadPosts();
//    long endTime = System.currentTimeMillis();
//    long duration = endTime - startTime;
//    log.info("총 소요 시간: {} 밀리초", duration); 18분 41초 소요
//  }
//
//  private void loadPosts() throws IOException, CsvException {
//    List<String[]> rows = csvUtil.readCsv("csv-init-data/Institution_list.csv");
//    for (String[] row : rows) {
//      processRow(row);
//    }
//  }
//
//  private void processRow(String[] row) {
//    String region = row[0];
//    String name = row[1];
//    String parentInstitution = row[2];
//    String address = row[3];
//    String regionalMilitaryOfficeCode = mapRegionalMilitaryOfficeToEnglish(row[4]);
//    String phoneNumber = row[5];
//    String diseaseRestriction = row[6];
//    boolean sexualCriminalRecordRestriction = "y".equalsIgnoreCase(row[7]);
//    String tag = mapTagToEnglish(row[8]);
//    String category = mapCategoryToEnglish(row[9]);
//
//    regionalMilitaryOfficeRepository.findByOfficeName(regionalMilitaryOfficeCode)
//        .ifPresent(regionalMilitaryOffice -> {
//          institutionCategoryRepository.findByCategoryName(category)
//              .ifPresent(institutionCategory -> {
//
//                Institution.InstitutionBuilder institutionBuilder = Institution.builder()
//                    .region(region)
//                    .name(name)
//                    .parentInstitution(parentInstitution)
//                    .address(address)
//                    .phoneNumber(phoneNumber)
//                    .regionalMilitaryOffice(regionalMilitaryOffice)
//                    .sexualCriminalRecordRestriction(sexualCriminalRecordRestriction)
//                    .institutionCategory(institutionCategory);
//
//                if (tag != null) {
//                  institutionTagRepository.findByTagName(tag)
//                      .ifPresentOrElse(institutionTag -> {
//                        institutionBuilder.tag(institutionTag);
//
//                        saveInstitution(institutionBuilder.build(), diseaseRestriction);
//                      }, () -> {
//
//                        saveInstitution(institutionBuilder.build(), diseaseRestriction);
//                      });
//                } else {
//
//                  saveInstitution(institutionBuilder.build(), diseaseRestriction);
//                }
//              });
//        });
//  }
//
//  private void saveInstitution(Institution institution, String diseaseRestriction) {
//
//    Institution savedInstitution = institutionRepository.save(institution);
//
//    if (diseaseRestriction == null || diseaseRestriction.isEmpty()) {
//      return;
//    }
//
//    String[] diseaseNames = diseaseRestriction.split("/");
//    for (String diseaseName : diseaseNames) {
//      String englishDiseaseName = mapDiseaseToEnglish(diseaseName);
//      diseaseRestrictionRepository.findByDiseaseName(englishDiseaseName).ifPresent(disease -> {
//        InstitutionDiseaseRestrictionId id = new InstitutionDiseaseRestrictionId(
//            savedInstitution.getId(), disease.getId());
//        InstitutionDiseaseRestriction institutionDiseaseRestriction = new InstitutionDiseaseRestriction(
//            id, savedInstitution, disease);
//        institutionDiseaseRestrictionRepository.save(institutionDiseaseRestriction);
//      });
//    }
//  }
//
//  private String mapCategoryToEnglish(String category) {
//    switch (category) {
//      case "사회복지시설":
//        return "SOCIAL_WELFARE";
//      case "공공단체":
//        return "PUBLIC_ORGANIZATION"; // 추가된 부분
//      case "국가기관":
//        return "NATIONAL_AGENCY"; // 추가된 부분
//      case "지방자치단체":
//        return "LOCAL_GOVERNMENT"; // 추가된 부분
//      default:
//        return category;
//    }
//  }
//
//  private String mapTagToEnglish(String tag) {
//    switch (tag) {
//      case "노인복지시설":
//        return "ELDERLY_WELFARE_FACILITY";
//      case "아동복지시설":
//        return "CHILD_WELFARE_FACILITY";
//      case "장애인복지시설":
//        return "DISABLED_WELFARE_FACILITY";
//      case "복지관":
//        return "WELFARE_CENTER";
//      case "지역주민시설":
//        return "LOCAL_RESIDENT_FACILITY";
//      case "보육시설":
//        return "DAYCARE_CENTER";
//      case "자활시설":
//        return "SELF_SUFFICIENCY_CENTER";
//      case "정신보건시설":
//        return "MENTAL_HEALTH_FACILITY";
//      case "여성복지시설":
//        return "WOMEN_WELFARE_FACILITY";
//      case "청소년복지시설":
//        return "YOUTH_WELFARE_FACILITY";
//      case "노숙인복지시설":
//        return "HOMELESS_WELFARE_FACILITY";
//      case "결핵한센시설":
//        return "TUBERCULOSIS_HANSEN_FACILITY";
//      default:
//        return tag;
//    }
//  }
//
//  private String mapRegionalMilitaryOfficeToEnglish(String officeName) {
//    switch (officeName) {
//      case "서울":
//        return "SEOUL";
//      case "부산.울산":
//        return "BUSAN_ULSAN";
//      case "대구.경북":
//        return "DAEGU_GYEONGBUK";
//      case "경인":
//        return "GYEONGIN";
//      case "광주.전남":
//        return "GWANGJU_JEONNAM";
//      case "대전.충남":
//        return "DAEJEON_CHUNGNAM";
//      case "강원":
//        return "GANGWON";
//      case "충북":
//        return "CHUNGBUK";
//      case "전북":
//        return "JEONBUK";
//      case "경남":
//        return "GYEONGNAM";
//      case "제주":
//        return "JEJU";
//      case "인천":
//        return "INCHEON";
//      case "경기북부":
//        return "GYEONGGI_NORTH";
//      case "강원영동":
//        return "GANGWON_YEONGDONG";
//      default:
//        return officeName;
//    }
//  }
//
//  private String mapDiseaseToEnglish(String diseaseName) {
//    switch (diseaseName) {
//      case "정신과질환":
//        return "MENTAL_ILLNESS";
//      case "경련성":
//        return "SEIZURE";
//      case "문신자해":
//        return "TATTOO_SELF_HARM";
//      case "척추질환":
//        return "SPINE_DISEASE";
//      case "기관지천식":
//        return "BRONCHIAL_ASTHMA";
//      case "아토피피부염":
//        return "ATOPIC_DERMATITIS";
//      default:
//        return diseaseName;
//    }
//  }
//}