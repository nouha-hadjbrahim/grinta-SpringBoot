package com.esprit.backendsport.Repository.community;

import com.esprit.backendsport.Entities.community.PostLike;
import com.esprit.backendsport.Entities.community.Post;
import com.esprit.backendsport.Entities.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    boolean existsByPostAndUser(Post post, User user);
    List<PostLike> findByPost(Post post);
}
