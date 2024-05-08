package tn.esprit.pi.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import tn.esprit.pi.dto.requests.AuthenticationRequest;
import tn.esprit.pi.dto.requests.ModifyUserProfileRequest;
import tn.esprit.pi.dto.requests.RegisterRequest;
import tn.esprit.pi.dto.responses.AuthenticationResponse;
import tn.esprit.pi.dto.responses.MessageResponse;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.services.AuthenticationService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService ;


    @GetMapping("/test")
    public String test() {
        return "connected" ;
    }



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        try {
            authenticationService.register(request) ;
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())) ;
        }
        return ResponseEntity.ok()
                .body(new MessageResponse("check your email to verify your account !"));
    }

    @PostMapping("/resendVerification")
    public ResponseEntity<?> resendVerification(@RequestBody String email) {

        try {
            authenticationService.sendVerifyAccountEmail(email); ;
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())) ;
        }
        return ResponseEntity.ok()
                .body(new MessageResponse("check your email to verify your account !"));
    }



    @GetMapping("/verifyAccount")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String verifyToken) {
        try {
            authenticationService.verifyAccount(verifyToken) ;
        }catch(Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())) ;
        }
        return ResponseEntity.ok()
                .body("<div style='width:100% ; height : 100vh ;  display :flex ; gap : 20px ; justify-content : center ; align-items : center ; flex-direction : column ; '>" +
                        "<p style='font-weight : 1000;'>your account is verified ! </p>"
                        + "<img style='width : 150px ; height : 150px;'/>"
                        + "</div>");

    }
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse responseBody ;
        try {
            responseBody = authenticationService.authenticate(request);
        }catch(Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())) ;
        }
        ResponseCookie jwtCookie = ResponseCookie
                .from("jwtCookie", responseBody.getRefreshToken())
                .path("/api/v1/auth/refresh_token").maxAge(7 * 24 * 60 * 60)
                .httpOnly(true)
                .build();
        responseBody.setRefreshToken("hi ! you can find me in the cookie");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(responseBody);
    }
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> modifyUserProfile(@PathVariable Integer userId, @RequestBody ModifyUserProfileRequest request) {
        return authenticationService.modifyUserProfile(userId, request);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = authenticationService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponse> refresh_token(HttpServletRequest request) throws IOException {
        Cookie cookie = WebUtils.getCookie(request, "jwtCookie");
        final String refreshToken ;
        if(cookie != null)
        {
            refreshToken = cookie.getValue() ;
            System.out.println(refreshToken);
            AuthenticationResponse responseBody = authenticationService.refreshToken(request,refreshToken) ;
            ResponseCookie jwtCookie = ResponseCookie
                    .from("jwtCookie", responseBody.getRefreshToken())
                    .path("/api/v1/auth/refresh_token").maxAge(7 * 24 * 60 * 60)
                    .httpOnly(true)
                    .build();
            responseBody.setRefreshToken("hi ! you can find me in the cookie");

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(responseBody);
        }
        else throw new IOException("no cookie found, you are logged out") ;

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout()
    {
        ResponseCookie cookie ;
        try {
            authenticationService.logout();
            final String newCookieValue = "you_are_logged_out";
            cookie = ResponseCookie.from("jwtCookie", newCookieValue).path("/api/v1/auth/refresh_token").maxAge(10)
                    .httpOnly(true).build();
        }catch(Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage())) ;
        }

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));


    }
}
