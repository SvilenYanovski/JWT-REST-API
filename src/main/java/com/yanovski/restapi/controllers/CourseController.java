package com.yanovski.restapi.controllers;

import com.yanovski.restapi.dtos.CourseDTO;
import com.yanovski.restapi.services.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
}
