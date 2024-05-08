package tn.esprit.pi.controllers;

import tn.esprit.pi.repositories.OptionRepository;
import tn.esprit.pi.repositories.QuestionRepository;
import tn.esprit.pi.services.OptionServiceC;
import tn.esprit.pi.services.QuestionServiceC;
import tn.esprit.pi.entities.Question;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@AllArgsConstructor
@RequestMapping("/questions")
public class QuestionRestController {

    @Autowired


    QuestionServiceC questionService;
    OptionServiceC optionService;
    QuestionRepository questionRepository ;
    OptionRepository optionRepository;




    @GetMapping("/retrieve-all-questions")
    public List<Question> getQuestions() {
        List<Question> listQuestions = questionService.getAllQuestions();
        return listQuestions;
    }


    @GetMapping("/retrieve-selected-questions")
    public List<Question> getRandomQuestions() {
        List<Question> selectedQuestions = questionService.selectRandomQuestions();
        return selectedQuestions ;
    }



    @GetMapping("/retrieve-question/{question-id}")
    public Question retrieveQuestion(@PathVariable("question-id") Long QuestionId) {
        return questionService.getQuestionById(QuestionId);
    }



    @PostMapping("/add-question")
    public Question addQuestion(@RequestBody Question question) {
        Question question1 = questionService.createQuestion(question);
        return question1;
    }



    @DeleteMapping("/remove-question/{question-id}")
    public void removeQuestion(@PathVariable("question-id") Long questionId) {
        questionService.deleteQuestion(questionId);
    }


    @PutMapping("/update-question")
    public Question updateQuestion(@RequestBody Question question) {
        Question question1 = questionService.updateQuestion(question);
        return question1;
    }























}