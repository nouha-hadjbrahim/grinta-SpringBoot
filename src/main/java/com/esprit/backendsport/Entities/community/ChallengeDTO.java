package com.esprit.backendsport.Entities.community;

import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDTO {
    private Long challengeId;
    private String title;
    private String description;
    private String type;
    private boolean isTrending;
    private Integer targetValue;
    private String reward;
    private Date endDate;
    private int participantsCount;

    // Optional: Static method to easily convert Entity to DTO
    public static ChallengeDTO fromEntity(Challenge challenge) {
        if (challenge == null) return null;
        return ChallengeDTO.builder()
                .challengeId(challenge.getChallengeId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .type(challenge.getType() != null ? challenge.getType().name() : null)
                .isTrending(challenge.isTrending())
                .targetValue(challenge.getTargetValue())
                .reward(challenge.getReward())
                .endDate(challenge.getEndDate())
                .participantsCount(challenge.getParticipantsCount())
                .build();
    }

    // Optional: Method to convert DTO to Entity
    public Challenge toEntity() {
        return Challenge.builder()
                .challengeId(this.challengeId)
                .title(this.title)
                .description(this.description)
                .type(this.type != null ? ChallengeType.valueOf(this.type) : null)
                .isTrending(this.isTrending)
                .targetValue(this.targetValue)
                .reward(this.reward)
                .endDate(this.endDate)
                .participantsCount(this.participantsCount)
                .build();
    }
}
