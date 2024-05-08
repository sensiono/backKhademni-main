package tn.esprit.pi.feedback;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import tn.esprit.pi.books.Book;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Feedback implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double note; //1..5 stars
    private String comment;
    @CreatedBy
//    @Column(nullable = true, updatable = false)
    private Integer createdBy;


    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}

