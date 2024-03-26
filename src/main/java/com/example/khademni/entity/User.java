package com.example.khademni.entity;

import jakarta.persistence.*;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomUtilisateur;
    private String motDePasse;
    private String email;
    private String profile;
    @ElementCollection
    private List<String> competences;

    private String nomEntreprise;
    private String adresseEntreprise;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Evaluation> evaluations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Evenement> evenements;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Reclamation> reclamations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Paiement> paiements;

}