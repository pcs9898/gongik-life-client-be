package org.example.gongiklifeclientbeinstitutionservice.repository.elasticsearch;

import java.util.List;
import org.example.gongiklifeclientbeinstitutionservice.document.InstitutionDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionSearchRepository extends
    ElasticsearchRepository<InstitutionDocument, Long> {

//  @Query("{'bool': {'must': [{'exists': {'field': 'id'}}]}}")
//  boolean existsAny();

  // cursor 기반 페이징 검색
  List<InstitutionDocument> findByNameContainingAndIdGreaterThanOrderByIdAsc(
      String name,
      String id,
      Pageable pageable
  );

  // 첫 페이지 검색
  List<InstitutionDocument> findByNameContainingOrderByIdAsc(
      String name,
      Pageable pageable
  );

}