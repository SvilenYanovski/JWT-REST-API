package com.yanovski.restapi.controllers;

import com.yanovski.restapi.dtos.CourseDTO;
import com.yanovski.restapi.dtos.ScoreDTO;
import com.yanovski.restapi.dtos.UserDTO;
import com.yanovski.restapi.services.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/course")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> create(@RequestBody CourseDTO course) {
        log.info("Creating new course: {}", course);
        courseService.create(course);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/course/{courseId}/student/{studentUsername}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<UserDTO> assignStudentToCourse(@PathVariable Long courseId, @PathVariable String studentUsername) {
        log.info("Assigning student Username:{} to a course ID:{}", studentUsername, courseId);
        UserDTO result = courseService.assignStudentToCourse(studentUsername, courseId);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/course/{courseId}/teacher/{teacherUsername}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseDTO> assignTeacherToCourse(@PathVariable Long courseId, @PathVariable String teacherUsername) {
        log.info("Assigning teacher Username:{} to a course ID:{}", teacherUsername, courseId);
        CourseDTO result = courseService.assignTeacherToCourse(teacherUsername, courseId);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/course/{courseId}/student/{studentUsername}/score/{scoreValue}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ScoreDTO> assignAScoreToStudentsCourse(@PathVariable Long courseId,
                                                                 @PathVariable String studentUsername,
                                                                 @PathVariable Double scoreValue) {
        log.info("Assigning a score:{} to a student Username:{} in a course ID:{}", scoreValue, studentUsername, courseId);
        ScoreDTO result = courseService.assignAScoreToStudentsCourse(studentUsername, courseId, scoreValue);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
