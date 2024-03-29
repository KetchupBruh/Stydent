package com.example.backend2.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewViewAllDto {
        private Integer id;
        private String categoryName;
        private String courseName;
        private String courseFullName;
        private String gradesReceived;
        private String instructorName;
        private Integer ratingOfInteresting;
        private Integer ratingOfGroupWork;
        private Integer ratingOfGradeCollect;
        private Integer ratingOfEasyExam;
        private Integer ratingOfIndividualWork;
        private String work;
        private String emailOwner;
        private String reviewDescription;
        private Instant reviewCreatedOn;
}
