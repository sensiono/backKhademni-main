package com.example.khademni.Service;

import com.example.khademni.entity.Test;

import java.util.List;

public interface TestService {
    List<Test> getAllTests();
    Test getTestById(Long test_id);
    Test createTest(Test test);
    Test updateTest(Test test);
    void deleteTest(Long test_id);
    Test getRandomTestByStack(String stack);

}
