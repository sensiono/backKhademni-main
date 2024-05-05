package tn.esprit.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pi.entities.ResetPasswordToken;
import tn.esprit.pi.entities.User;

import java.util.Optional;
@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken,Integer> {

    void removeAllByUser(User user) ;
    Optional<ResetPasswordToken> findByToken(String token) ;

}
