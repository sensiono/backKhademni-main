package tn.esprit.pi.controllers;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import tn.esprit.pi.dto.requests.AuthenticationRequest;
import tn.esprit.pi.dto.requests.RegisterRequest;
import tn.esprit.pi.dto.requests.ResetPasswordRequest;
import tn.esprit.pi.dto.responses.AuthenticationResponse;
import tn.esprit.pi.dto.responses.MessageResponse;
import tn.esprit.pi.entities.ResetPasswordToken;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.repositories.ResetPasswordTokenRepository;
import tn.esprit.pi.repositories.UserRepository;
import tn.esprit.pi.services.AuthenticationService;
import java.io.IOException;


import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService ;
    private final UserRepository userRepository;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;



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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody String email) throws MessagingException {
        User user = userRepository.findByEmail(email.trim()).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email not found"));
        }

        String token = authenticationService.createResetPasswordToken(user);
        authenticationService.sendResetPasswordEmail(user, token);

        return ResponseEntity.ok(new MessageResponse("Password reset link sent to email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        // Check if the provided reset token is valid
        ResetPasswordToken token = resetPasswordTokenRepository.findByToken(request.getToken())
                .orElse(null);

        if (token == null || LocalDateTime.now().isAfter(token.getExpiryDateTime())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired token"));
        }

        // Now that you have a valid token, call the `updatePassword` method with the token and the new password
        authenticationService.updatePassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(new MessageResponse("Password has been reset"));
    }











}