package com.yanovski.restapi.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
public class HomeController {

    @GetMapping("/")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    @GetMapping("/private/all")
    @PreAuthorize("hasRole('USER') or hasRole('STUDENT') or hasRole('TEACHER')")
    public ResponseEntity<String> all() {
        log.info("Accessing private all endpoint");
        return new ResponseEntity<>("Private for ALL roles.", HttpStatus.OK);
    }

    @GetMapping("/private/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<String> student() {
        log.info("Accessing private student endpoint");
        return new ResponseEntity<>("Private for STUDENT role.", HttpStatus.OK);
    }

    @GetMapping("/private/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> teacher() {
        log.info("Accessing private teacher endpoint");
        return new ResponseEntity<>("Private for TEACHER role.", HttpStatus.OK);
    }
}
