package tn.esprit.pi.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import tn.esprit.pi.entities.Reclamation;
import tn.esprit.pi.entities.TypeReclamation;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.services.EmailService;
import tn.esprit.pi.services.IReclamationServices;
import tn.esprit.pi.services.ReclamationTypeGuesser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pi.services.EmailService;
import tn.esprit.pi.services.IReclamationServices;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/reclamation")

@RestController
public class ReclamationRestController {



    @Autowired
    private IReclamationServices iReclamationServices;

    @Autowired
    private EmailService emailService;

    public ReclamationRestController(
            IReclamationServices iReclamationServices,
            EmailService emailService
    ) {
        this.iReclamationServices = iReclamationServices;
        this.emailService = emailService;
    }




    @PostMapping("/addRec")
    public Reclamation addReclamation( @RequestBody Reclamation reclamation) {

        // Guess the type based on the description
        TypeReclamation guessedType = ReclamationTypeGuesser.guessType(reclamation.getDescription());

        reclamation.setType(guessedType); // Set the guessed type

        // Set the createdAt field to the current date and time
        reclamation.setCreatedAt(LocalDateTime.now());
        Reclamation newReclamation = iReclamationServices.addReclamation(reclamation);

        // Send email notification
        emailService.sendEmail(
                "daadsoufi01@gmail.com",
                "New Reclamation Created",
                "A new reclamation with ID: " + newReclamation.getIdRec() + " has been created."
        );

        return newReclamation;
    }

    @PostMapping("/sendPDF")
    public ResponseEntity<?> sendPDF(@RequestParam("file") MultipartFile file) {
        try {
            emailService.sendEmailWithAttachment(
                    "recipient@example.com", // Change to your desired email
                    "Reclamation PDF",
                    "Please find the PDF attached.",
                    file
            );
            return ResponseEntity.ok("PDF sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending PDF");
        }
    }

    @PutMapping("/updateRec")
    public ResponseEntity<Reclamation> updateReclamation(
            @RequestBody Reclamation reclamation,
            @AuthenticationPrincipal User currentUser // Inject the current authenticated user
    ) {
        try {
            Reclamation updatedReclamation = iReclamationServices.updateReclamation(reclamation, currentUser);
            return ResponseEntity.ok(updatedReclamation);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(null); // Return 403 Forbidden if unauthorized
        }
    }


    @DeleteMapping("/deleteRec/{idRec}")
    public void deleteReclamation(@PathVariable Long idRec) {
        iReclamationServices.deleteReclamation(idRec);
    }

    @GetMapping("/getRec/{idRec}")
    public Reclamation getIReclamation(@PathVariable Long idRec) {
        return iReclamationServices.getById(idRec);
    }


    @GetMapping("/allRec")
    public List<Reclamation> getAllReclamations() {
        return iReclamationServices.getAll();
    }

/*
    @PostMapping("/addRecUser/{idUser}")
    public Reclamation assignToUser(@PathVariable Long idUser, @RequestBody Reclamation reclamation) {
        Reclamation createdReclamation = iReclamationServices.addReclamationAndAssignToUser(reclamation, idUser);
        return createdReclamation;
    }

    @GetMapping("/getreclamationparuser/{idUser}")
    public List<Reclamation> getReclamationsByUser(@PathVariable Long idUser) {

        System.out.println("User ID : "+idUser);
        return iReclamationServices.getReclamationsByUser(idUser);
    }*/
}
