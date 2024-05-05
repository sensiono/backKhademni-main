package tn.esprit.pi.entities;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
public class Offre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String description;
    private String competenceRequise;
    private String duree;
    private double remuneration;
    private boolean favoris;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
