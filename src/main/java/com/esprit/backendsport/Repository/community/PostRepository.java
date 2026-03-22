package com.esprit.backendsport.Repository.community;

import com.esprit.backendsport.Entities.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
