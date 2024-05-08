package tn.esprit.pi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pi.entities.Evenement;
import tn.esprit.pi.repositories.IEvenementRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EvenementServiceImp implements IEvenementService {

    private final IEvenementRepository evenementRepository;

    @Autowired
    public EvenementServiceImp(IEvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }

    @Override
    public Evenement saveEvenement(Evenement evenement) {
        return evenementRepository.save(evenement);
    }

    @Override
    public Evenement updateEvenement(Evenement evenement) {
        return evenementRepository.save(evenement);
    }

    @Override
    public void deleteEvenement(Long id) {
        evenementRepository.deleteById(id);
    }

    @Override
    public Evenement getEvenementById(Long id) {
        Optional<Evenement> optionalEvenement = evenementRepository.findById(id);
        return optionalEvenement.orElse(null);
    }

    @Override
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }
}
