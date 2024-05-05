package tn.esprit.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.entities.VerifyAccountToken;

import java.util.Optional;
@Repository
public interface VerifyAccountTokenRepository extends JpaRepository<VerifyAccountToken,Integer> {
    void removeAllByUser(User user) ;
    Optional<VerifyAccountToken> findByToken(String token) ;
}
