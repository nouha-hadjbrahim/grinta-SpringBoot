package com.esprit.backendsport.Entities.community;

import com.esprit.backendsport.Entities.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostImage> images = new ArrayList<>();

    @Column(columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private int likesCount = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private int commentsCount = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private int sharesCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType type;
}
