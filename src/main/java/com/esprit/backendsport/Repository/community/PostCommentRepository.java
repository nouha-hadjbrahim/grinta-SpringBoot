package com.esprit.backendsport.Repository.community;

import com.esprit.backendsport.Entities.community.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
