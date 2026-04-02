// src/main/java/com/main/icrsbackend/model/interview/MentorFollower.java
package com.main.icrsbackend.model.interview;

import com.main.icrsbackend.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "mentor_followers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"mentor_id", "student_id"})
        }
)
@Getter
@Setter
public class MentorFollower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;
}