package tn.esprit.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.pi.entities.Evenement;

public interface IEvenementRepository extends JpaRepository<Evenement, Long> {
}