package com.bilgeadam.repository.entity;

import com.bilgeadam.repository.enums.FollowState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tbl_following")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Following {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long userid;
    Long followinguserid;
    FollowState followState;
    @Embedded
    BaseEntity baseEntity;
}
