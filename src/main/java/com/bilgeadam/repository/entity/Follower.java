package com.bilgeadam.repository.entity;

import com.bilgeadam.repository.enums.FollowState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbl_follower")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long followeruserid;
    FollowState followState;
    @Embedded
    BaseEntity baseEntity;
}
