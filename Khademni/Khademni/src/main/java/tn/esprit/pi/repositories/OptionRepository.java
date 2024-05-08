package tn.esprit.pi.repositories;


import tn.esprit.pi.entities.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {
}
