package tn.esprit.pi.config;

import org.springframework.security.core.userdetails.UserDetailsService;
import tn.esprit.pi.services.JwtService;

public class JwtAuthenticationFilterImpl extends JwtAuthenticationFilter {
    public JwtAuthenticationFilterImpl(JwtService jwtService, UserDetailsService userDetailsService) {
        super(jwtService, userDetailsService);
    }
}
