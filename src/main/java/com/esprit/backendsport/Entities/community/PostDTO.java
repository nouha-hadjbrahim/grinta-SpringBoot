package com.esprit.backendsport.Entities.community;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PostDTO {
    private Long postId;
    private Long userId;
    private String userEmail; // To show who made the post
    private String content;
    private Date createdAt;
    private String imageUrl;
    private int likesCount;
    private int commentsCount;
    private int sharesCount;
    private PostType type;
}
