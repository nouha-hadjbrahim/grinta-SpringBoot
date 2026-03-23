package com.esprit.backendsport.Entities.community;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PostCommentDTO {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String userEmail;
    private String content;
    private Date createdAt;
    private Long parentCommentId;
    private int likesCount;
    private java.util.List<Long> likedByUserIds;
}