package tn.esprit.pi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pi.dto.requests.UpdateUserRequest;
import tn.esprit.pi.dto.responses.ImageResponse;
import tn.esprit.pi.dto.responses.MessageResponse;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.services.UserService;

import java.security.Principal;
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService ;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrent(Principal connectedUser) {
        final User responseBody ;
        try {
            responseBody = userService.getCurrentUser(connectedUser);
        }catch(Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())) ;
        }
        return ResponseEntity.ok().body(responseBody) ;
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCurrent(Principal connectedUser, @RequestBody UpdateUserRequest updatedUser) {
        try {
            userService.updateCurrentUser(connectedUser,updatedUser);
        }catch(Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())) ;
        }
        return ResponseEntity.ok().body("User updated successfully !") ;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCurrent(Principal connectedUser) {
        try {
            userService.deleteCurrentUser(connectedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
        return ResponseEntity.ok().body("User deleted successfully !");
    }


    @PostMapping("/add")
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

}
