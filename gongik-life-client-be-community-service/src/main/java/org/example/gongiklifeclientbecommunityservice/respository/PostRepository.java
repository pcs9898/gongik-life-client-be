package org.example.gongiklifeclientbecommunityservice.respository;

import java.util.UUID;
import org.example.gongiklifeclientbecommunityservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

}
