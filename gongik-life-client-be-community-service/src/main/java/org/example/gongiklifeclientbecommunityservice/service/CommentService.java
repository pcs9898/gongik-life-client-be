package org.example.gongiklifeclientbecommunityservice.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gongiklifeclientbecommunityservice.respository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

  private final CommentRepository commentRepository;


  public void deleteAllCommentsByPost(String postId) {
    try {
      commentRepository.softDeleteAllByPostId(UUID.fromString(postId));
    } catch (Exception e) {
      log.error("Error deleting comments by postId: {}", postId, e);
      throw e;
    }
  }
}
