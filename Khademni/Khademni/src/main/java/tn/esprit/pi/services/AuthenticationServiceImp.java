package tn.esprit.pi.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import tn.esprit.pi.dto.requests.AuthenticationRequest;
import tn.esprit.pi.dto.requests.RegisterRequest;
import tn.esprit.pi.dto.responses.AuthenticationResponse;
import tn.esprit.pi.entities.ResetPasswordToken;
import tn.esprit.pi.entities.Role;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.entities.VerifyAccountToken;
import tn.esprit.pi.repositories.ResetPasswordTokenRepository;
import tn.esprit.pi.repositories.UserRepository;
import tn.esprit.pi.repositories.VerifyAccountTokenRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
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

    private static final String[] ALLOWED_DOMAINS = {
            "gmail.com",
            "esprit.tn",
            "yahoo.fr",
            "yahoo.com",
            "hotmail.com"
    };

    @Value("${BASE_API_URL}")
    private String baseApiUrl ;

    public void register(RegisterRequest request) throws MessagingException {

        // Extract domain from email
        String email = request.getEmail().trim();
        String[] emailParts = email.split("@");
        String domain = emailParts.length > 1 ? emailParts[1] : "";

        // Default role to "Etudiant"
        Role role = Role.Etudiant;

        // Check if the domain is not in the allowed list
        boolean isStandardDomain = false;
        for (String allowedDomain : ALLOWED_DOMAINS) {
            if (domain.equalsIgnoreCase(allowedDomain)) {
                isStandardDomain = true;
                break;
            }
        }

        if (!isStandardDomain) {
            role = Role.Entreprise; // If not a standard domain, assign "Entreprise"
        }

        User  user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
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

    public String createResetPasswordToken(User user) {
        // Remove existing tokens for this user
        resetPasswordTokenRepository.removeAllByUser(user);

        UUID uuid = UUID.randomUUID();
        String tokenValue = uuid.toString();

        ResetPasswordToken token = ResetPasswordToken.builder()
                .token(tokenValue)
                .expiryDateTime(LocalDateTime.now().plusHours(2)) // Token valid for 2 hours
                .user(user)
                .build();

        resetPasswordTokenRepository.save(token);

        return tokenValue;
    }


    public void sendResetPasswordEmail(User user, String token) throws MessagingException {
        // Generate the reset password link
        String resetLink = baseApiUrl + "/api/v1/auth/reset-password?token=" + token;

        // Set up the context for Thymeleaf
        Context context = new Context();
        context.setVariable("fullName", user.getFirstname() + " " + user.getLastname());
        context.setVariable("link", resetLink);

        // Process the template with Thymeleaf
        String emailContent = templateEngine.process("resetPasswordEmail", context);

        // Create a MIME message for the email
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(new InternetAddress("noreply@yourdomain.com")); // Change this to your sender email
        mimeMessageHelper.setTo(user.getEmail());
        mimeMessageHelper.setSubject("Reset Your Password");
        mimeMessageHelper.setText(emailContent, true); // Set to true for HTML content

        // Send the email
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

    public void updatePassword(String token, String newPassword) {
        // Find the reset token
        ResetPasswordToken resetToken = resetPasswordTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        // Check if the token is expired
        if (LocalDateTime.now().isAfter(resetToken.getExpiryDateTime())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        // Get the user associated with the token
        User user = resetToken.getUser();

        // Encode the new password
        String encodedPassword = passwordEncoder.encode(newPassword);

        // Update the user's password
        user.setPassword(encodedPassword);

        // Save the user to persist the change
        userRepository.save(user);

        // Invalidate the token after updating the password
        resetPasswordTokenRepository.delete(resetToken);
    }





}
