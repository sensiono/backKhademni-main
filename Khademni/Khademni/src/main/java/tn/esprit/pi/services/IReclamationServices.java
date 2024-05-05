package tn.esprit.pi.services;

import tn.esprit.pi.entities.Reclamation;
import tn.esprit.pi.entities.User;

import java.util.List;

public interface IReclamationServices {

    Reclamation addReclamation(Reclamation reclamation);

    Reclamation updateReclamation(Reclamation reclamation, User currentUser);

    void deleteReclamation(Long idRec);

    Reclamation getById(Long idRec);
    List<Reclamation> getAll();
/*
    Reclamation addReclamationAndAssignToUser(Reclamation reclamation, Long idUser);

    List<Reclamation> getReclamationsByUser(Long idRec);*/
}
