package com.esprit.backendsport.Entities.community;

import com.esprit.backendsport.Entities.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Temporal(TemporalType.DATE)
    private Date createdAt;

    private String imageUrl;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int likesCount;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int commentsCount;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int sharesCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType type;
}
