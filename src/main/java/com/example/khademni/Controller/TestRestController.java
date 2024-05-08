package com.example.khademni.Controller;

import com.example.khademni.Repository.OptionRepository;
import com.example.khademni.Repository.QuestionRepository;
import com.example.khademni.Repository.TestRepository;
import com.example.khademni.Service.QuestionServiceC;
import com.example.khademni.Service.TestServiceC;
import com.example.khademni.Service.TestService;
import com.example.khademni.entity.Option;
import com.example.khademni.entity.Question;
import com.example.khademni.entity.Test;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.NoSuchElementException;


import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@AllArgsConstructor
@RequestMapping("/tests")
public class TestRestController {
    @Autowired
    private TestService testService;

    QuestionRepository questionRepository ;
    TestRepository testRepository ;
    QuestionServiceC questionService;
    OptionRepository optionRepository;


    @GetMapping("/retrieve-all-tests")
    public List<Test> getTests() {
        List<Test> listTests = testService.getAllTests();
        return listTests;
    }


    @GetMapping("/{test_id}/questions")
    public List<Question> getQuestionsForTest(@PathVariable Long test_id) {
        Test test = testService.getTestById(test_id);
        return test.getQuestions();
    }


    @GetMapping("/retrieve-test/{test-id}")
    public Test retrieveTest(@PathVariable("test-id") Long TestId) {
        return testService.getTestById(TestId);
    }


    @PostMapping("/add-test")
    public Test addTest(@RequestBody Test test) {
        Test test1 = testService.createTest(test);
        return test1;
    }


    @PostMapping("/{test_id}/questions/{question_id}")
    public Test addQuestionToTest(@PathVariable("test_id") Long test_id, @PathVariable("question_id") Long question_id) {
        Test test = testRepository.findById(test_id).orElse(null);
        Question question = questionRepository.findById(question_id).orElseThrow(() -> new EntityNotFoundException("Entity with ID " + question_id + " not found"));
        test.getQuestions().add(question);
        return testRepository.save(test);
    }


    /*@DeleteMapping("/remove-test/{test_id}")
    public void removeTest(@PathVariable("test_id") Long test_id) {
        testService.deleteTest(test_id);*/

    @DeleteMapping("/remove-test/{test_id}")
    public ResponseEntity<?> deleteTest(@PathVariable Long test_id) {
        try {
            testService.deleteTest(test_id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NoSuchElementException e) {
            // Handle case where test with the given ID is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Test not found");
        } catch (Exception e) {
            // Handle other exceptions with a generic error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }




    public Question changeQuestion() {
        List<Question> listQuestions = questionService.getAllQuestions();
        Collections.shuffle(listQuestions);
        Question randQuestion = listQuestions.get(0);
        return randQuestion;
    }


    @PostMapping("/create-test")
    public Test createTestWithQuestionsAndOptions(@RequestBody Test test) {
        Test test1 = new Test();
        test1.setName(test.getName());
        test1.setDescription(test.getDescription());
        test1.setStack(test.getStack());
        test1.setLevel(test.getLevel());
        List<Question> questions = new ArrayList<>();
        for (Question question : test.getQuestions()) {
            Question question1 = new Question();
            question1.setText(question.getText());
            question1.setCorrect_option(question.getCorrect_option());

            List<Option> options = new ArrayList<>();
            for (Option option : question.getOptions()) {
                Option option1 = new Option();
                option1.setText(option.getText());
                option.setQuestion(question);
                options.add(option);
            }

            question.setOptions(options);
            questions.add(question);
        }

        test.setQuestions(questions);

        Test savedTest = testService.createTest(test);

        return savedTest;
    }

    //Permits to modify a specific test
    @PutMapping("/update-test/{test_id}")
    public Test updateTest(@PathVariable("test_id") long test_id, @RequestBody Test test) {
        Optional<Test> testData = testRepository.findById(test_id);
        Test test1 = testData.get();

        test1.setName(test.getName());
        test1.setDescription(test.getDescription());
        test1.setStack(test.getStack());
        test1.setLevel(test.getLevel());
        List<Question> questions = new ArrayList<>();
        for (Question question : test.getQuestions()) {
            Question question1 = new Question();
            question1.setText(question.getText());
            question1.setCorrect_option(question.getCorrect_option());

            List<Option> options = new ArrayList<>();
            for (Option option : question.getOptions()) {
                Option option1 = new Option();
                option1.setText(option.getText());
                option1.setQuestion(question1);
                options.add(option1);
            }

            question1.setOptions(options);
            questions.add(question1);
        }

        test1.setQuestions(questions);

        Test savedTest = testService.createTest(test1);

        return savedTest;
    }

    /*@GetMapping("/random/{stack}")
    public ResponseEntity<Test> getRandomTestByStack(@PathVariable String stack) {
        Test test = testService.getRandomTestByStack(stack);
        if (test != null) {
            return new ResponseEntity<>(test, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }*/


}
