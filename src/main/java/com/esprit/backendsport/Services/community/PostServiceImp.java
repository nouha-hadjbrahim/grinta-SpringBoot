package com.esprit.backendsport.Services.community;

import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Entities.community.CreatePostRequestDTO;
import com.esprit.backendsport.Entities.community.Post;
import com.esprit.backendsport.Entities.community.PostDTO;
import com.esprit.backendsport.Repository.User.UserRepository;
import com.esprit.backendsport.Repository.community.PostRepository;
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
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    private PostDTO toDto(Post post) {
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
                .build();
    }
}
