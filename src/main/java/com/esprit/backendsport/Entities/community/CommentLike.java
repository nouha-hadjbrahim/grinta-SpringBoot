package com.esprit.backendsport.Entities.community;

import com.esprit.backendsport.Entities.User.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comment_likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "commentId", nullable = false)
    private PostComment comment;
}