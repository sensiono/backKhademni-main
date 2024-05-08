package tn.esprit.pi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pi.entities.Offre;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.services.IOffreService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/offre")
@RestController
public class OffreController {
    private IOffreService offreService;

    @Autowired
    public OffreController(IOffreService offreService) {
        this.offreService = offreService;
    }

    @PostMapping("/create")
    public Offre createOffre(@RequestBody Offre offre) {
        return offreService.createOffre(offre);
    }

    @GetMapping("/{id}")
    public Offre getOffreById(@PathVariable Long id) {
        return offreService.getOffreById(id);
    }

    @GetMapping("/all")
    public List<Offre> getAllOffres() {
        return offreService.getAllOffres();
    }

    @PutMapping("/update")
    public ResponseEntity<Offre> updateOffre(
            @RequestBody Offre offre,
            @AuthenticationPrincipal User currentUser // Inject the current authenticated user
    ) {
        try {
            Offre updatedOffre = offreService.updateOffre(offre, currentUser);
            return ResponseEntity.ok(updatedOffre);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Return 403 Forbidden if unauthorized
        }
    }


    @DeleteMapping("/delete")
    public void deleteOffre(@RequestBody Offre offre) {
        offreService.deleteOffre(offre);
    }


}