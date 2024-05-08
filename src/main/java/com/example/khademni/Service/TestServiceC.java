package com.example.khademni.Service;

import com.example.khademni.Repository.QuestionRepository;
import com.example.khademni.Repository.TestRepository;
import com.example.khademni.entity.Question;
import com.example.khademni.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;








import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Random;


@Service
public class TestServiceC implements  TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public TestServiceC(TestRepository testRepository, QuestionRepository questionRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
    }

    public Test createTest(Test test){
        return testRepository.save(test);
    }

    public Test addQuestionToTest(Long testId, Long question_id) {
        Test test = testRepository.findById(testId).orElseThrow(EntityNotFoundException::new);;
        Question question = questionRepository.findById(question_id).orElseThrow(EntityNotFoundException::new);
        test.getQuestions().add(question);
        return testRepository.save(test);
    }

    public Test updateTest(Test test) {
        return testRepository.save(test);
    }

    public void deleteTest(Long test_id) {

        testRepository.deleteById(test_id);
    }

    public List<Test> getAllTests() {

        return (List<Test>) testRepository.findAll();
    }

    public Test getTestById(Long test_id) {
        Optional<Test> optionalTest = testRepository.findById(test_id);
        if (optionalTest.isPresent()) {
            return optionalTest.get();
        } else {
            // Handle the case where the test is not found, such as throwing an exception or returning null
            // For example:
            throw new EntityNotFoundException("Test not found with ID: " + test_id);
        }
    }
    @Override
    public Test getRandomTestByStack(String stack) {
        List<Test> tests = testRepository.findByStack(stack);
        if (!tests.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(tests.size());
            return tests.get(randomIndex);
        }
        return null; // Or throw an exception if no test is found
    }



}
