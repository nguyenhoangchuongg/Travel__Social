package com.app.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Entity
@Table(name = "Likes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Like extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "FOLLOWER_ID", referencedColumnName = "ID")
    private Account account;
}
