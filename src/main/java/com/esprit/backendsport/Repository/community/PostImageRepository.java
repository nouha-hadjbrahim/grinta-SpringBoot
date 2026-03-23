package com.esprit.backendsport.Repository.community;

import com.esprit.backendsport.Entities.community.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Integer> {
    List<PostImage> findByPostPostIdOrderByOrderAsc(Long postId);
}
