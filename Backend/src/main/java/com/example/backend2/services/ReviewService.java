package com.example.backend2.services;

import com.example.backend2.Dto.ReviewCreateDto;
import com.example.backend2.Dto.ReviewDetailDto;
import com.example.backend2.Dto.ReviewEditDto;
import com.example.backend2.Dto.ReviewViewAllDto;
import com.example.backend2.Entity.CategoryCourse;
import com.example.backend2.Entity.Course;
import com.example.backend2.Entity.Review;
import com.example.backend2.Repository.CourseRepository;
import com.example.backend2.Repository.ReviewRepository;
import com.example.backend2.Utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    public Review create(ReviewCreateDto reviewNew){
        Authentication roleMail = SecurityContextHolder.getContext().getAuthentication();

        Review review = modelMapper.map(reviewNew, Review.class);
        Course course = courseRepository.getById(reviewNew.getCourseIdcourse());
        courseRepository.findById(reviewNew.getCourseIdcourse()).orElseThrow(()->{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Please Check your Course");
        });
        review.setCourseIdcourse(course);

        Instant currentInstant = Instant.now();
        review.setReviewCreatedOn(currentInstant);

        System.out.println(roleMail);
        if(roleMail.getAuthorities().toString().equals("[st_group]")){
            if(!roleMail.getDetails().equals(review.getEmailOwner())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email not same");
            }
        }

        return reviewRepository.saveAndFlush(review);
    }

//    public List<ReviewViewAllDto> getReviewAll() {
//        List<Review> reviewAllList = reviewRepository.findAll(Sort.by("reviewCreatedOn").descending());
//        return listMapper.mapList(reviewAllList, ReviewViewAllDto.class, modelMapper);
//    }

    //V.1 list all
    public List<ReviewViewAllDto> getReviewAll() {
        List<Review> reviewAllList = reviewRepository.findAllReviewsWithCategoryName();
        List<ReviewViewAllDto> reviewViewAllDtos = new ArrayList<>();

        for (Review review : reviewAllList) {
            ReviewViewAllDto dto = new ReviewViewAllDto();
            dto.setId(review.getIdReview());
            dto.setGradesReceived(review.getGradesReceived());
            dto.setInstructorName(review.getInstructorName());
            dto.setRatingOfIndividualWork(review.getRatingOfIndividualWork());
            dto.setRatingOfEasyExam(review.getRatingOfEasyExam());
            dto.setRatingOfGradeCollect(review.getRatingOfGradeCollect());
            dto.setRatingOfInteresting(review.getRatingOfInteresting());
            dto.setRatingOfGroupWork(review.getRatingOfGroupWork());
            dto.setWork(review.getWork());
            dto.setReviewDescription(review.getReviewDescription());
            dto.setEmailOwner(review.getEmailOwner());

            // Map Course details
            Course course = review.getCourseIdcourse();
            if (course != null) {
                dto.setCourseName(course.getCourseName());
                dto.setCourseFullName(course.getCourseFullName());

                // Map Category details
                CategoryCourse categoryCourse = course.getCategoryCourseIdcategoryCourse();
                if (categoryCourse != null) {
                    dto.setCategoryName(categoryCourse.getCategoryName());
                }
            }

            dto.setReviewCreatedOn(review.getReviewCreatedOn());
            reviewViewAllDtos.add(dto);
        }

        return reviewViewAllDtos;
    }

    //V.2 list all
//    public Page<ReviewViewAllDto> getReviewAll(int page, int pageSize) {
//        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("reviewCreatedOn").descending());
//        Page<Review> reviewAllPage = reviewRepository.findAll(pageable);
//
//        return reviewAllPage.map(review -> {
//            ReviewViewAllDto dto = new ReviewViewAllDto();
//            dto.setId(review.getId());
//            dto.setGradesReceived(review.getGradesReceived());
//            dto.setInstructorName(review.getInstructorName());
//            dto.setRating(review.getRating());
//            dto.setWork(review.getWork());
//            dto.setReviewDescription(review.getReviewDescription());
//
//            // Map Course details
//            Course course = review.getCourseIdcourse();
//            if (course != null) {
//                dto.setCourseName(course.getCourseName());
//                dto.setCourseFullName(course.getCourseFullName());
//
//                // Map Category details
//                CategoryCourse categoryCourse = course.getCategoryCourseIdcategoryCourse();
//                if (categoryCourse != null) {
//                    dto.setCategoryName(categoryCourse.getCategoryName());
//                }
//            }
//
//            dto.setReviewCreatedOn(review.getReviewCreatedOn());
//            return dto;
//        });
//    }

    public ReviewDetailDto getDetail(Integer id) {
        Review review = reviewRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + " does't exist !!"));
        return modelMapper.map(review, ReviewDetailDto.class);
    }

    public void deleteReview(@PathVariable Integer reviewId) {
        Authentication roleMail = SecurityContextHolder.getContext().getAuthentication();

        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        reviewId + " does't exist !!"));

        System.out.println(roleMail);
        if(roleMail.getAuthorities().toString().equals("[st_group]")){
            if(!roleMail.getDetails().equals(review.getEmailOwner())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"It's not your post");
            }
        }

        reviewRepository.deleteById(reviewId);
    }

    public Review editReview(ReviewEditDto editReview, Integer id) {
        Authentication roleMail = SecurityContextHolder.getContext().getAuthentication();

        Review review = reviewRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        id + " does't exist !!"));

        review.setGradesReceived(editReview.getGradesReceived());
        review.setInstructorName(editReview.getInstructorName());
        review.setRatingOfIndividualWork(editReview.getRatingOfIndividualWork());
        review.setRatingOfEasyExam(editReview.getRatingOfEasyExam());
        review.setRatingOfGradeCollect(editReview.getRatingOfGradeCollect());
        review.setRatingOfInteresting(editReview.getRatingOfInteresting());
        review.setRatingOfGroupWork(editReview.getRatingOfGroupWork());
        review.setWork(editReview.getWork());
        review.setReviewDescription(editReview.getReviewDescription());

        System.out.println(roleMail);
        if(roleMail.getAuthorities().toString().equals("[st_group]")){
            if(!roleMail.getDetails().equals(review.getEmailOwner())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"It's not your post");
            }
        }

        return reviewRepository.saveAndFlush(review);
    }

//    public Page<ReviewViewAllDto> getReviewAllByEmailOwner(int page, int size) {
//        Authentication roleMail = SecurityContextHolder.getContext().getAuthentication();
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Review> reviewPage = reviewRepository.findAllReviewByEmailOwner(roleMail.getDetails().toString(), pageable);
//        return reviewPage.map(review -> {
//            ReviewViewAllDto dto = new ReviewViewAllDto();
//            dto.setId(review.getIdReview());
//            dto.setGradesReceived(review.getGradesReceived());
//            dto.setInstructorName(review.getInstructorName());
//            dto.setRating(review.getRating());
//            dto.setWork(review.getWork());
//            dto.setReviewDescription(review.getReviewDescription());
//            dto.setEmailOwner(review.getEmailOwner());
//
//            // Map Course details
//            Course course = review.getCourseIdcourse();
//            if (course != null) {
//                dto.setCourseName(course.getCourseName());
//                dto.setCourseFullName(course.getCourseFullName());
//
//                // Map Category details
//                CategoryCourse categoryCourse = course.getCategoryCourseIdcategoryCourse();
//                if (categoryCourse != null) {
//                    dto.setCategoryName(categoryCourse.getCategoryName());
//                }
//            }
//
//            dto.setReviewCreatedOn(review.getReviewCreatedOn());
//            return dto;
//        });
//    }

    public List<ReviewViewAllDto> getReviewAllByEmailOwner() {
        Authentication roleMail = SecurityContextHolder.getContext().getAuthentication();

        List<Review> reviewAllList = reviewRepository.findAllReviewByEmailOwner(roleMail.getDetails().toString());
        List<ReviewViewAllDto> reviewViewAllDtos = new ArrayList<>();

        for (Review review : reviewAllList) {
            ReviewViewAllDto dto = new ReviewViewAllDto();
            dto.setId(review.getIdReview());
            dto.setGradesReceived(review.getGradesReceived());
            dto.setInstructorName(review.getInstructorName());
            dto.setRatingOfIndividualWork(review.getRatingOfIndividualWork());
            dto.setRatingOfEasyExam(review.getRatingOfEasyExam());
            dto.setRatingOfGradeCollect(review.getRatingOfGradeCollect());
            dto.setRatingOfInteresting(review.getRatingOfInteresting());
            dto.setRatingOfGroupWork(review.getRatingOfGroupWork());
            dto.setWork(review.getWork());
            dto.setReviewDescription(review.getReviewDescription());
            dto.setEmailOwner(review.getEmailOwner());

            // Map Course details
            Course course = review.getCourseIdcourse();
            if (course != null) {
                dto.setCourseName(course.getCourseName());
                dto.setCourseFullName(course.getCourseFullName());

                // Map Category details
                CategoryCourse categoryCourse = course.getCategoryCourseIdcategoryCourse();
                if (categoryCourse != null) {
                    dto.setCategoryName(categoryCourse.getCategoryName());
                }
            }

            dto.setReviewCreatedOn(review.getReviewCreatedOn());
            reviewViewAllDtos.add(dto);
        }

        return reviewViewAllDtos;
    }

}
