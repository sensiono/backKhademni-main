package tn.esprit.pi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pi.entities.Evenement;
import tn.esprit.pi.services.IEvenementService;

import java.util.List;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    private final IEvenementService evenementService;

    @Autowired
    public EvenementController(IEvenementService evenementService) {
        this.evenementService = evenementService;
    }

    @PostMapping
    public ResponseEntity<Evenement> createEvenement(@RequestBody Evenement evenement) {
        Evenement createdEvenement = evenementService.saveEvenement(evenement);
        return new ResponseEntity<>(createdEvenement, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEvenementById(@PathVariable("id") Long id) {
        Evenement evenement = evenementService.getEvenementById(id);
        if (evenement != null) {
            return new ResponseEntity<>(evenement, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        List<Evenement> evenements = evenementService.getAllEvenements();
        return new ResponseEntity<>(evenements, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evenement> updateEvenement(@PathVariable("id") Long id, @RequestBody Evenement evenement) {
        Evenement existingEvenement = evenementService.getEvenementById(id);
        if (existingEvenement != null) {
            evenement.setId(id);
            Evenement updatedEvenement = evenementService.updateEvenement(evenement);
            return new ResponseEntity<>(updatedEvenement, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable("id") Long id) {
        Evenement existingEvenement = evenementService.getEvenementById(id);
        if (existingEvenement != null) {
            evenementService.deleteEvenement(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
