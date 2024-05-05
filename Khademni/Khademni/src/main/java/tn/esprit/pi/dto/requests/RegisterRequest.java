package tn.esprit.pi.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.pi.entities.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String firstname ;
    private String lastname ;
    private String email ;
    private String password ;
    private Role role ;
}
