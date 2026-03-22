package com.esprit.backendsport.Repository.community;

import com.esprit.backendsport.Entities.community.CommentLike;
import com.esprit.backendsport.Entities.community.PostComment;
import com.esprit.backendsport.Entities.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(PostComment comment, User user);
    List<CommentLike> findByComment(PostComment comment);
}