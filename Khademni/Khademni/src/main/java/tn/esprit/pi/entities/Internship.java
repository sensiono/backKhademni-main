package tn.esprit.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Internship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String town;
    private LocalDate startDate; //dateDebut
    @Enumerated(EnumType.STRING)
    private InternshipType internshipType; //type de stage ete,pfe ...
    private String formationType; //type de formation: commerce, compta, eco...
    private String competenceRequired; //competenceRequise /specialite
    private String duree;
    private boolean prime; // remuneration oui ou non
    private String currentLevelOfEducation;  //niveauDeFormationActuel bac+...
    private String logo;

    @OneToMany(mappedBy = "internship", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Candidature> listCandidatureInternship;

    public void addCandidatureInternship(Candidature candidature ){
        if (candidature != null){
            if(listCandidatureInternship == null){
                listCandidatureInternship = new HashSet<>();
            }
            // listCandidatureInternship n'est pas vide et candidature existe
            listCandidatureInternship.add(candidature);
        }

    }
}
