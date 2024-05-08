package tn.esprit.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tn.esprit.pi.entities.Reclamation;
import tn.esprit.pi.entities.User;

import java.util.List;

public interface IReclamationRepository extends JpaRepository<Reclamation, Long> {

    @Query("SELECT c FROM Reclamation c WHERE c.user.id = :id")
    public List<Reclamation> getReclamationByUsername(@Param("id") Integer id);

    List<Reclamation> findByDescriptionContaining(String keyword);

    Reclamation findTopByUserOrderByCreatedAtDesc(User user);
}
