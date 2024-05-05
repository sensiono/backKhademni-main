package tn.esprit.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Reclamation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRec;

    @Column(length = 1000)
    private String description;
    private String etat;
    @Enumerated(EnumType.STRING)
    private TypeReclamation type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt; // New field to store creation timestamp
/*
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reclamation")
    private List<Response> responses;*/
}