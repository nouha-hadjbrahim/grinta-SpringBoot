package com.esprit.backendsport.Repository.community;

import com.esprit.backendsport.Entities.community.PostComment;
import com.esprit.backendsport.Entities.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPost(Post post);
}
