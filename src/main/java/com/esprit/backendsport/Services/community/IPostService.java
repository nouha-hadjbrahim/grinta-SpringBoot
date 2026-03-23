package com.esprit.backendsport.Services.community;

import com.esprit.backendsport.Entities.community.CreatePostRequestDTO;
import com.esprit.backendsport.Entities.community.PostDTO;
import com.esprit.backendsport.Entities.community.PostCommentDTO;
import com.esprit.backendsport.Entities.community.CreateCommentRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IPostService {
    List<PostDTO> getAllPosts();
    Optional<PostDTO> getPostById(Long id);
    PostDTO savePost(CreatePostRequestDTO request, List<MultipartFile> files);
    PostDTO updatePost(Long id, CreatePostRequestDTO request, List<MultipartFile> files, List<Integer> deletedImageIds);
    void deletePost(Long id);

    // Likes & Comments
    void toggleLike(Long postId, Long userId);
    void toggleCommentLike(Long commentId, Long userId);
    PostCommentDTO addComment(Long postId, CreateCommentRequest request);
    List<PostCommentDTO> getCommentsByPost(Long postId);
    void deleteComment(Long commentId);
}
