package com.example.khademni.Service;

import com.example.khademni.entity.Question;

import java.util.List;

public interface QuestionService {
    List<Question> getAllQuestions();
    List<Question> selectRandomQuestions();
    Question getQuestionById(Long question_id);
    Question createQuestion(Question question);
    Question updateQuestion(Question question);
    void deleteQuestion(Long question_id);
}
