package com.esprit.backendsport.Entities.community;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePostRequestDTO {
    private Long userId;
    private String content;
    private String imageUrl;
    private PostType type;
}
