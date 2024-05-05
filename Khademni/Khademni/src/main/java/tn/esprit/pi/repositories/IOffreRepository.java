package tn.esprit.pi.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import tn.esprit.pi.entities.Offre;

public interface IOffreRepository extends JpaRepository<Offre, Long> {
}
