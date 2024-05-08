package tn.esprit.pi.services;

import tn.esprit.pi.entities.Offre;
import tn.esprit.pi.entities.User;

import java.util.List;

public interface IOffreService {
    Offre createOffre(Offre offre);
    Offre getOffreById(Long id);
    List<Offre> getAllOffres();
    Offre updateOffre(Offre offre, User currentUser);
    void deleteOffre(Offre offre);
}