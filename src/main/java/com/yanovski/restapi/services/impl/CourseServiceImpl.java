package com.yanovski.restapi.services.impl;

import com.yanovski.restapi.dtos.CourseDTO;
import com.yanovski.restapi.models.Course;
import com.yanovski.restapi.repositoties.CourseRepository;
import com.yanovski.restapi.services.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    private ModelMapper mapper = new ModelMapper();

    @Override
    public void create(CourseDTO courseDTO) {
        courseRepository.save(mapper.map(courseDTO, Course.class));
    }
}
