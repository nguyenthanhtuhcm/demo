package com.example.demo.service;

import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;
    /**
     * This method is used to authenticate a user by checking if the provided username and password match the stored credentials.
     *
     * @param request The authentication request containing the username and password.
     * @return true if the authentication is successful, false otherwise.
     * @throws AppException if the user does not exist or if the password does not match.
     */
    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            var token = request.getToken();
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            boolean isValid = signedJWT.verify(verifier);
            return IntrospectResponse.builder()
                    .valid(isValid && expirationTime != null && expirationTime.after(new Date()))
                    .build();
        } catch (Exception e) {
            log.error("Error verifying JWT: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHENTICATE);
        }
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!auth) {
            throw new AppException(ErrorCode.UNAUTHENTICATE);
        }
        String token = generateToken(user.getUsername());
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();

    }
//
    private String generateToken(String username) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("http://localhost:8080")
                .issueTime(new Date())
                .claim("userId", "Custom")
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error signing JWT: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }

}
