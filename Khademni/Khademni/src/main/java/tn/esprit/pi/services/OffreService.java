package tn.esprit.pi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pi.entities.Offre;
import tn.esprit.pi.repositories.IOffreRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OffreService implements IOffreService {
    private IOffreRepository offreRepository;

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
    public Offre updateOffre(Offre offre) {
        return offreRepository.save(offre);
    }

    @Override
    public void deleteOffre(Offre offre) {
        offreRepository.delete(offre);
    }
}