package tn.esprit.pi.services;

import org.springframework.scheduling.annotation.Async;
import tn.esprit.pi.entities.Reclamation;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.repositories.IReclamationRepository;
import tn.esprit.pi.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ReclamationServices implements IReclamationServices {

    private final IReclamationRepository ireclamationRepository;
    private final UserRepository userRepository;

    @Override
    public Reclamation addReclamation(Reclamation reclamation) {
        reclamation.setCreatedAt(LocalDateTime.now());
        // Save the new reclamation
        Reclamation newReclamation = ireclamationRepository.save(reclamation);

        // Schedule asynchronous update to set etat to "solved" after 10 seconds
        CompletableFuture.runAsync(() -> markReclamationAsSolved(newReclamation.getIdRec()));

        return newReclamation;
    }

    @Async // Marks this method as asynchronous
    public CompletableFuture<Void> markReclamationAsSolved(Long reclamationId) {
        try {
            // Wait for 10 seconds before updating
            TimeUnit.SECONDS.sleep(50);

            // Retrieve the reclamation by ID
            Reclamation reclamation = getById(reclamationId);

            // Update the etat to "solved"
            reclamation.setEtat("solved");

            // Save the updated reclamation
            ireclamationRepository.save(reclamation);

            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            throw new RuntimeException("Error while marking reclamation as solved", e);
        }
    }

    @Override
    public Reclamation updateReclamation(Reclamation reclamation, User currentUser) {
        Reclamation existingReclamation = getById(reclamation.getIdRec());

        // Check if the current user is authorized to update the reclamation
        if (!existingReclamation.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You are not authorized to update this reclamation");
        }

        existingReclamation.setDescription(reclamation.getDescription());
        existingReclamation.setEtat(reclamation.getEtat());

        return ireclamationRepository.save(existingReclamation);
    }

    @Override
    public void deleteReclamation(Long idRec) {
        ireclamationRepository.deleteById(idRec);
    }

    @Override
    public Reclamation getById(Long idRec) {
        return ireclamationRepository.findById(idRec)
                .orElseThrow(() -> new EntityNotFoundException("Reclamation with id " + idRec + " not found"));
    }

    @Override
    public List<Reclamation> getAll() {
        return (List<Reclamation>) ireclamationRepository.findAll();
    }
}
