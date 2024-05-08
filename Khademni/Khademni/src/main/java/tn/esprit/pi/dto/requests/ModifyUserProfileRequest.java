package tn.esprit.pi.dto.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyUserProfileRequest {
    private String oldPassword;
    private String newPassword;
    private String Firstname;
    private String email;
    private String tel;
    private String image;
}
