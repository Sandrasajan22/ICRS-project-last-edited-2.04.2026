package com.main.icrsbackend.model.jobseeker;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_academics")
@Getter
@Setter
public class Academics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "degree")
    private String degree;

    @Column(name = "field")
    private String field;

    @Column(name = "college")
    private String college;

    @Column(name = "university")
    private String university;

    @Column(name = "year")
    private String year;

    @Column(name = "marks")
    private String marks;
}