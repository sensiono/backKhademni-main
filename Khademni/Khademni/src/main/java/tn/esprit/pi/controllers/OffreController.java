package tn.esprit.pi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pi.entities.Offre;
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
    public Offre updateOffre(@RequestBody Offre offre) {
        return offreService.updateOffre(offre);
    }

    @DeleteMapping("/delete")
    public void deleteOffre(@RequestBody Offre offre) {
        offreService.deleteOffre(offre);
    }
}