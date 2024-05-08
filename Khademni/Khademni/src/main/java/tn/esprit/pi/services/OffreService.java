package tn.esprit.pi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pi.entities.Offre;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.repositories.IOffreRepository;
import tn.esprit.pi.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class OffreService implements IOffreService {
    private IOffreRepository offreRepository;
    private  UserRepository userRepository;

    @Autowired
    public OffreService(IOffreRepository offreRepository) {
        this.offreRepository = offreRepository;
    }

    @Override
    public Offre createOffre(Offre offre) {
        return offreRepository.save(offre);
    }
    @Override
    public Offre getOffreById(Long id) {
        Optional<Offre> optionalOffre = offreRepository.findById(id);
        return optionalOffre.orElse(null); // ou lancer une exception si nécessaire
    }

    @Override
    public List<Offre> getAllOffres() {
        return offreRepository.findAll();
    }



    @Override
    public Offre updateOffre(Offre offre, User currentUser) {
        Offre existingOffre = getOffreById(offre.getId());

        // Vérifier si l'utilisateur actuel est autorisé à mettre à jour l'offre
        if (!existingOffre.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You are not authorized to update this offer");
        }

        // Mettre à jour les champs de l'offre existante
        //existingOffre.setTitre(offre.getTitre());
        existingOffre.setDescription(offre.getDescription());
        existingOffre.setCompetenceRequise(offre.getCompetenceRequise());
        existingOffre.setDuree(offre.getDuree());
        existingOffre.setRemuneration(offre.getRemuneration());
        //existingOffre.setFavoris(offre.isFavoris());
        existingOffre.setTypeOffre(offre.getTypeOffre());

        return offreRepository.save(existingOffre);
    }


    @Override
    public void deleteOffre(Offre offre) {
        offreRepository.delete(offre);
    }
}


