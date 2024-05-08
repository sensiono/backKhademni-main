package tn.esprit.pi.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tn.esprit.pi.controllers.AuthenticationController;
import tn.esprit.pi.dto.requests.AuthenticationRequest;
import tn.esprit.pi.dto.requests.ModifyUserProfileRequest;
import tn.esprit.pi.dto.requests.RegisterRequest;
import tn.esprit.pi.dto.responses.AuthenticationResponse;
import tn.esprit.pi.entities.Role;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.entities.VerifyAccountToken;
import tn.esprit.pi.repositories.ResetPasswordTokenRepository;
import tn.esprit.pi.repositories.UserRepository;
import tn.esprit.pi.repositories.VerifyAccountTokenRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImp implements AuthenticationService {

    private final UserRepository userRepository ;
    private final PasswordEncoder passwordEncoder ;
    private final JwtService jwtService ;
    private final AuthenticationManager authenticationManager ;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository ;
    private final VerifyAccountTokenRepository verifyAccountTokenRepository ;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);


    @Value("${BASE_API_URL}")
    private String baseApiUrl ;


    public void register(RegisterRequest request) throws MessagingException {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.Etudiant)
                .registrationDate(new Date())
                .enabled(false)
                .build() ;

        userRepository.save(user) ;
        sendVerifyAccountEmail(user.getEmail());
    }

    public String createVerifyAccountToken( User concernedUser ) {
        verifyAccountTokenRepository.removeAllByUser(concernedUser) ;
        UUID uuid = UUID.randomUUID();
        String tokenValue = uuid.toString() ;
        VerifyAccountToken token = VerifyAccountToken.builder()
                .token(tokenValue)
                .expiryDateTime(LocalDateTime.now().plusDays(1))
                .user(concernedUser)
                .build() ;
        verifyAccountTokenRepository.save(token) ;
        return token.getToken() ;

    }

    public void sendVerifyAccountEmail(String email) throws  MessagingException {


        InternetAddress recipientAddress = new InternetAddress();
        email = email.trim() ;
        recipientAddress.setAddress(email);
        User concernedUser = userRepository.findByEmail(email).orElse(null) ;
        if(concernedUser == null) throw new RuntimeException("problem in registration !") ;
        if(concernedUser.isEnabled()) throw new RuntimeException("user is already verified, proceed to login !") ;
        String fullName = concernedUser.getFirstname() + " " + concernedUser.getLastname() ;
        String verifyToken = createVerifyAccountToken(concernedUser) ;
        String link = baseApiUrl + "/api/v1/auth/verifyAccount?token=" + verifyToken ;
        Context context = new Context();
        context.setVariable("fullName",fullName);
        context.setVariable("link",link);
        String processedHTMLTemplate = templateEngine.process("verifyAccountEmaill",context) ;


        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(new InternetAddress("noreply@farmease.com"));
        mimeMessageHelper.setTo(recipientAddress);
        mimeMessageHelper.setSubject("verify account");
        mimeMessageHelper.setText(processedHTMLTemplate, true);

        javaMailSender.send(mimeMessage);
    }


    public void verifyAccount(String token) {
        VerifyAccountToken givenToken = verifyAccountTokenRepository.findByToken(token).orElse(null) ;
        if(givenToken != null) {
            LocalDateTime now = LocalDateTime.now();
            if(now.isAfter(givenToken.getExpiryDateTime())) throw new RuntimeException("it has been more than a day, request another verification email !")  ;
            User concernedUser = givenToken.getUser() ;
            concernedUser.setEnabled(true);


        }  else {
            throw new RuntimeException("there was a problem verifying the user, request another verification email !") ;
        }
    }



    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        ) ;
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow() ;
        var jwtToken = jwtService.generateToken(user) ;
        var refreshToken = jwtService.generateRefreshToken(user) ;
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build() ;
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request, String refreshToken) throws IOException {

        final String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var newAccessToken = jwtService.generateToken(user);
                var newRefreshToken = jwtService.generateRefreshToken(user);
                return  AuthenticationResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();
            }
        }
        else throw new IOException("token not valid") ;
        return null;
    }

    public void logout() {
        SecurityContextHolder.clearContext();

    }

    public ResponseEntity<?> modifyUserProfile(Integer userId, ModifyUserProfileRequest request) {
        try {
            logger.info("Received request to modify user profile for user ID: {}", userId);

            // Fetch the user from the repository
            User user = userRepository.findById((userId))
                    .orElseThrow(() -> {
                        logger.error("User not found for ID: {}", userId);
                        return new RuntimeException("User not found");
                    });

            // Log the user details
            logger.info("Found user with ID {}: {}", userId, user);

            // Check old password
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                logger.error("Incorrect old password for user ID: {}", userId);
                throw new RuntimeException("Incorrect old password");
            }

            // Log the request details
            logger.info("Received modify profile request: {}", request);

            // Update user details
            user.setFirstname(request.getFirstname());
            user.setEmail(request.getEmail());
            // Update other fields as needed

            // If a new password is provided, update it
            if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }

            // Save the updated user to the repository
            userRepository.save(user);

            logger.info("User profile successfully updated for user ID: {}", userId);

            // Return success response
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Log any exceptions that occur
            logger.error("Error modifying user profile for user ID {}: {}", userId, e.getMessage(), e);
            // Return an appropriate error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error modifying user profile");
        }
    }


    public List<User> getAllUsers () {
        return userRepository.findAll();
    }

}
