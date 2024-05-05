package tn.esprit.pi.services;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pi.dto.requests.UpdateUserRequest;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.UUID;
@Service
@AllArgsConstructor
public class UserServiceImp implements UserService{

    private final UserRepository userRepository ;
    private final AuthenticationService authenticationService ;


    @Override
    public User getCurrentUser(Principal connectedUser) {
        return (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
    }

    @Override
    public void updateCurrentUser(Principal connectedUser, UpdateUserRequest updatedUser) {
        User currentUser = getCurrentUser(connectedUser) ;

        if(updatedUser.getFirstname() != null) currentUser.setFirstname(updatedUser.getFirstname())  ;
        if(updatedUser.getLastname() != null) currentUser.setLastname(updatedUser.getLastname());
        if(updatedUser.getEmail() != null) currentUser.setEmail(updatedUser.getEmail());
        if(updatedUser.getPassword() != null) currentUser.setPassword(updatedUser.getPassword());

        userRepository.save(currentUser) ;
    }

    @Override
    public void deleteCurrentUser(Principal connectedUser) {
        User currentUser = getCurrentUser(connectedUser) ;
        authenticationService.logout();
        userRepository.delete(currentUser);
    }

    @Override
    public String currentUploadDirectory( Principal connectedUser) {
        User current = getCurrentUser(connectedUser) ;
        return  "src/main/resources/user_images/"  + current.getId() + current.getFirstname() ;
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }


}
