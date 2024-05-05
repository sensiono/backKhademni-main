package tn.esprit.pi.services;

import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pi.dto.requests.UpdateUserRequest;
import tn.esprit.pi.entities.User;

import java.io.IOException;
import java.security.Principal;

public interface UserService {
    public User getCurrentUser(Principal connectedUser) ;

    public void updateCurrentUser(Principal connectedUser, UpdateUserRequest updatedUser) ;

    public void deleteCurrentUser(Principal connectedUser) ;

    public String currentUploadDirectory( Principal connectedUser) ;

    User addUser(User user);

}
