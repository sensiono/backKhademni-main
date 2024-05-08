package tn.esprit.pi.services;

import tn.esprit.pi.repositories.QuestionRepository;
import tn.esprit.pi.entities.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class QuestionServiceC implements QuestionService {
    @Autowired
    QuestionRepository questionRepository ;
    private final Random random = new Random();
    @Override
    public List<Question> getAllQuestions() {
        return (List<Question>) questionRepository.findAll();
    }

    @Override
    public List<Question> selectRandomQuestions() {
        List<Question> allQuestions = getAllQuestions();
        List<Question> selectedQuestions = new ArrayList<>();

        // Check if there are at least five questions available
        if (allQuestions.size() >= 5) {
            // Pick five random questions
            while (selectedQuestions.size() < 5) {
                Question question = allQuestions.get(random.nextInt(allQuestions.size()));
                if (!selectedQuestions.contains(question)) {
                    selectedQuestions.add(question);
                }
            }
        } else {
            // If there are less than five questions available, return all questions
            selectedQuestions.addAll(allQuestions);
        }

        return selectedQuestions;
    }

    @Override
    public Question getQuestionById(Long question_id) {
        return questionRepository.findById(question_id).get();
    }

    @Override
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long question_id) {
        questionRepository.deleteById(question_id);
    }


}
