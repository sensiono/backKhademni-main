package tn.esprit.pi.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import tn.esprit.pi.entities.Reclamation;

import java.util.List;

public interface IReclamationRepository extends CrudRepository<Reclamation,Long> {

    @Query("SELECT c FROM Reclamation c WHERE c.user.id = :id")
    public List<Reclamation> getReclamationByUsername(@Param("id") Integer id);
}
