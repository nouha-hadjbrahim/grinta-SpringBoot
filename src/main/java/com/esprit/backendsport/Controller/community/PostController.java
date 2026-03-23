package com.esprit.backendsport.Controller.community;

import com.esprit.backendsport.Entities.community.CreatePostRequestDTO;
import com.esprit.backendsport.Entities.community.PostDTO;
import com.esprit.backendsport.Entities.community.PostCommentDTO;
import com.esprit.backendsport.Entities.community.CreateCommentRequest;
import com.esprit.backendsport.Services.community.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final IPostService postService;

    @GetMapping
    public List<PostDTO> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public PostDTO createPost(@ModelAttribute CreatePostRequestDTO request,
                              @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        return postService.savePost(request, files);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<PostDTO> updatePost(@PathVariable Long id,
                                              @ModelAttribute CreatePostRequestDTO request,
                                              @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                              @RequestParam(value = "deletedImageIds", required = false) List<Integer> deletedImageIds) {
        try {
            return ResponseEntity.ok(postService.updatePost(id, request, files, deletedImageIds));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        postService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // Likes
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
        try {
            postService.toggleLike(postId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Comments
    @PostMapping("/{postId}/comments")
    public ResponseEntity<PostCommentDTO> addComment(@PathVariable Long postId, @RequestBody CreateCommentRequest request) {
        try {
            return ResponseEntity.ok(postService.addComment(postId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> toggleCommentLike(@PathVariable Long commentId, @RequestParam Long userId) {
        try {
            postService.toggleCommentLike(commentId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<PostCommentDTO>> getComments(@PathVariable Long postId) {
        try {
            return ResponseEntity.ok(postService.getCommentsByPost(postId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
