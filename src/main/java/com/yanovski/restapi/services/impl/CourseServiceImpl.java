package com.yanovski.restapi.services.impl;

import com.yanovski.restapi.dtos.CourseDTO;
import com.yanovski.restapi.dtos.ScoreDTO;
import com.yanovski.restapi.dtos.UserDTO;
import com.yanovski.restapi.models.Course;
import com.yanovski.restapi.models.Role;
import com.yanovski.restapi.models.Score;
import com.yanovski.restapi.models.User;
import com.yanovski.restapi.repositoties.CourseRepository;
import com.yanovski.restapi.repositoties.ScoreRepository;
import com.yanovski.restapi.repositoties.UserRepository;
import com.yanovski.restapi.services.CourseService;
import com.yanovski.restapi.services.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ModelMapper mapper;

    @Override
    public void create(CourseDTO courseDTO) {
        courseRepository.save(mapper.map(courseDTO, Course.class));
    }

    @Override
    public UserDTO assignStudentToCourse(String studentUsername, Long courseId) {
        Optional<User> user = userRepository.findByUsername(studentUsername);
        Optional<Course> course = courseRepository.findById(courseId);
        if (user.isPresent() && course.isPresent()) {
            User usr = user.get();
            Course crs = course.get();
            String studentRole = roleService.getRoles().stream()
                    .filter(r -> r.getRoleName().equals(Role.Name.ROLE_STUDENT))
                    .map(r -> r.getRoleName().name())
                    .findFirst().orElseThrow(() -> new EntityNotFoundException("Student Role not found!"));

            if (!isInRole(usr, studentRole)) {
                log.error("The user is not a student: {}", usr);
                return null;
            }

            usr.getCourses().add(crs);
            return mapper.map(userRepository.save(usr), UserDTO.class);
        }
        return null;
    }

    @Override
    public ScoreDTO assignAScoreToStudentsCourse(String studentUsername, Long courseId, Double scoreValue) {
        Optional<User> user = userRepository.findByUsername(studentUsername);
        Optional<Course> course = courseRepository.findById(courseId);
        if (user.isPresent() && course.isPresent()) {
            User usr = user.get();
            Course crs = course.get();
            String studentRole = roleService.getRoles().stream()
                    .filter(r -> r.getRoleName().equals(Role.Name.ROLE_STUDENT))
                    .map(r -> r.getRoleName().name())
                    .findFirst().orElseThrow(() -> new EntityNotFoundException("Student Role not found!"));

            if (!isInRole(usr, studentRole)) {
                log.error("The user is not a student: {}", usr);
                return null;
            }

            if (!isInCourse(usr, crs)) {
                log.error("The user: {} is not in the course: {}", usr, crs);
                return null;
            }

            Score score = new Score();
            score.setCourse(crs);
            score.setStudent(usr);
            score.setScore(scoreValue);
            score.setDate(LocalDate.now());

            return mapper.map(scoreRepository.save(score), ScoreDTO.class);
        }
        return null;
    }

    @Override
    public CourseDTO assignTeacherToCourse(String teacherUsername, Long courseId) {
        Optional<User> teacher = userRepository.findByUsername(teacherUsername);
        Optional<Course> course = courseRepository.findById(courseId);
        if (teacher.isPresent() && course.isPresent()) {
            User usr = teacher.get();
            Course crs = course.get();
            String teacherRole = roleService.getRoles().stream()
                    .filter(r -> r.getRoleName().equals(Role.Name.ROLE_TEACHER))
                    .map(r -> r.getRoleName().name())
                    .findFirst().orElseThrow(() -> new EntityNotFoundException("Teacher Role not found!"));

            if (!isInRole(usr, teacherRole)) {
                log.error("The user is not a teacher: {}", usr);
                return null;
            }

            crs.setTeacher(usr);
            return mapper.map(courseRepository.save(crs), CourseDTO.class);
        }
        return null;
    }

    private boolean isInCourse(User usr, Course crs) {
        Optional<Course> course = usr.getCourses().stream()
                .filter(c -> c.getId() == crs.getId())
                .findFirst();

        return course.isPresent();
    }

    private boolean isInRole(User usr, String studentRole) {
        Optional<Role> role = usr.getRoles().stream()
                .filter(r -> r.getRoleName().name().equals(studentRole))
                .findFirst();
        return role.isPresent();
    }
}
