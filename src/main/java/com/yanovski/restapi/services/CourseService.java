package com.yanovski.restapi.services;

import com.yanovski.restapi.dtos.CourseDTO;
import com.yanovski.restapi.dtos.ScoreDTO;
import com.yanovski.restapi.dtos.UserDTO;

public interface CourseService {
    void create(CourseDTO courseDTO);

    UserDTO assignStudentToCourse(String studentUsername, Long courseId);

    ScoreDTO assignAScoreToStudentsCourse(String studentUsername, Long courseId, Double scoreValue);

    CourseDTO assignTeacherToCourse(String teacherUsername, Long courseId);
}
