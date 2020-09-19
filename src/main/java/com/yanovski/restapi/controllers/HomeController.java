package com.yanovski.restapi.controllers;

import com.yanovski.restapi.dtos.TestDto;
import com.yanovski.restapi.services.TestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class HomeController {
    private final Logger logger = LogManager.getLogger(HomeController.class);

    @Autowired
    private TestService testService;

    @GetMapping("/")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }

    @GetMapping("/private")
    public ResponseEntity<TestDto> homeObject() {
        logger.info("Accessing private test endpoint");
        return new ResponseEntity<>(testService.getTest(), HttpStatus.OK);
    }
}
