package com.esprit.backendsport.Entities.community;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private Long userId;
    private String content;
    private Long parentCommentId;
}