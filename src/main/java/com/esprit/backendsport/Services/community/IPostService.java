package com.esprit.backendsport.Services.community;

import com.esprit.backendsport.Entities.community.CreatePostRequestDTO;
import com.esprit.backendsport.Entities.community.PostDTO;

import java.util.List;
import java.util.Optional;

public interface IPostService {
    List<PostDTO> getAllPosts();
    Optional<PostDTO> getPostById(Long id);
    PostDTO savePost(CreatePostRequestDTO request);
    void deletePost(Long id);
}
