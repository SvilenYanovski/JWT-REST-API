package com.yanovski.restapi.services.impl;

import com.yanovski.restapi.dtos.TestDto;
import com.yanovski.restapi.services.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestServiceImpl implements TestService {
    @Override
    public TestDto getTest() {
        TestDto dto = new TestDto();
        dto.setTest("test");
        return dto;
    }
}
