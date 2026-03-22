package com.esprit.backendsport.Services.community;

import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Entities.community.*;
import com.esprit.backendsport.Repository.User.UserRepository;
import com.esprit.backendsport.Repository.community.PostRepository;
import com.esprit.backendsport.Repository.community.PostLikeRepository;
import com.esprit.backendsport.Repository.community.PostCommentRepository;
import com.esprit.backendsport.Repository.community.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImp implements IPostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostCommentRepository postCommentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PostDTO> getPostById(Long id) {
        return postRepository.findById(id).map(this::toDto);
    }

    @Override
    public PostDTO savePost(CreatePostRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Post post = Post.builder()
                .user(user)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .type(request.getType())
                .createdAt(new Date())
                .likesCount(0)
                .commentsCount(0)
                .sharesCount(0)
                .build();

        Post savedPost = postRepository.save(post);
        return toDto(savedPost);
    }

    @Override
    public PostDTO updatePost(Long id, CreatePostRequestDTO request) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

       
        if(request.getContent() != null) {
            existingPost.setContent(request.getContent());
        }
        if(request.getImageUrl() != null) {
            existingPost.setImageUrl(request.getImageUrl());
        }
        if(request.getType() != null) {
            existingPost.setType(request.getType());
        }

        Post updatedPost = postRepository.save(existingPost);
        return toDto(updatedPost);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public void toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<PostLike> existingLike = postLikeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            postLikeRepository.delete(existingLike.get());
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        } else {
            PostLike newLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .createdAt(new Date())
                    .build();
            postLikeRepository.save(newLike);
            post.setLikesCount(post.getLikesCount() + 1);
        }
        postRepository.save(post);
    }

    @Override
    public PostCommentDTO addComment(Long postId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostComment parent = null;
        if (request.getParentCommentId() != null) {
            parent = postCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        PostComment comment = PostComment.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .createdAt(new Date())
                .parentComment(parent)
                .likesCount(0)
                .build();

        PostComment savedComment = postCommentRepository.save(comment);

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return toCommentDto(savedComment);
    }

    @Override
    public void toggleCommentLike(Long commentId, Long userId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndUser(comment, user);

        if (existingLike.isPresent()) {
            commentLikeRepository.delete(existingLike.get());
            comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
        } else {
            CommentLike newLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(newLike);
            comment.setLikesCount(comment.getLikesCount() + 1);
        }
        postCommentRepository.save(comment);
    }

    @Override
    public List<PostCommentDTO> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return postCommentRepository.findByPost(post).stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    private PostDTO toDto(Post post) {
        List<Long> likedUsers = postLikeRepository.findByPost(post).stream()
                .map(like -> like.getUser().getId())
                .collect(Collectors.toList());

        return PostDTO.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getId())
                .userEmail(post.getUser().getEmail())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .imageUrl(post.getImageUrl())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .sharesCount(post.getSharesCount())
                .type(post.getType())
                .likedByUserIds(likedUsers)
                .build();
    }

    private PostCommentDTO toCommentDto(PostComment comment) {
        List<Long> likedUsers = commentLikeRepository.findByComment(comment).stream()
                .map(like -> like.getUser().getId())
                .collect(Collectors.toList());

        return PostCommentDTO.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getPostId())
                .userId(comment.getUser().getId())
                .userEmail(comment.getUser().getEmail())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .likesCount(comment.getLikesCount())
                .likedByUserIds(likedUsers)
                .build();
    }
}
