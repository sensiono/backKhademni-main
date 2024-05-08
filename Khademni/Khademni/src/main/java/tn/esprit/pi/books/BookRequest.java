package tn.esprit.pi.books;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequest {
        //         Integer id;
        String Author;
        String title;
        String isbn;
        String synopsis;

}
