package com.esprit.backendsport.Entities.community;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostImageDTO {
    private Integer imageId;
    private String imageUrl;
    private Integer order;
}
