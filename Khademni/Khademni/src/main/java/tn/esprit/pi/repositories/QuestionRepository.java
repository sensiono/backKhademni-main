package tn.esprit.pi.repositories;

import tn.esprit.pi.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}
