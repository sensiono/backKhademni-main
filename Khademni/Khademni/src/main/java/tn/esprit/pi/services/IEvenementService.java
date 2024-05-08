package tn.esprit.pi.services;

import tn.esprit.pi.entities.Evenement;

import java.util.List;

public interface IEvenementService {
    Evenement saveEvenement(Evenement evenement);

    Evenement updateEvenement(Evenement evenement);

    void deleteEvenement(Long id);

    Evenement getEvenementById(Long id);

    List<Evenement> getAllEvenements();
}
