package com.example.demo.service;

import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.request.LogoutRequest;
import com.example.demo.dto.request.RefreshRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.example.demo.dto.response.LogoutResponse;
import com.example.demo.entity.InvalidateToken;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.InvalidateTokenRepository;
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
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidateTokenRepository invalidateTokenRepository;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.expiration-duration}")
    protected long EXPIRATION_DURATION;

    @NonFinal
    @Value("${jwt.refresh-duration}")
    protected long REFRESH_DURATION;

    /**
     * This method is used to authenticate a user by checking if the provided username and password match the stored credentials.
     *
     * @param request The authentication request containing the username and password.
     * @return true if the authentication is successful, false otherwise.
     * @throws AppException if the user does not exist or if the password does not match.
     */
    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean valid = false;
        try {
            var token = request.getToken();
            verifyToken(token, false);
            valid = true;

        } catch (AppException | ParseException | JOSEException e) {
            log.error("Error verifying JWT: {}", e.getMessage());
        }
        return IntrospectResponse.builder()
                .valid(valid)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!auth) {
            throw new AppException(ErrorCode.UNAUTHENTICATE);
        }
        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();

    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        SignedJWT signedJWT = verifyToken(token, true);
        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(signedJWT.getJWTClaimsSet().getJWTID())
                .expireAt(signedJWT.getJWTClaimsSet().getExpirationTime())
                .build();
        invalidateTokenRepository.save(invalidateToken);

    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var token = request.getToken();
        SignedJWT signedJWT = verifyToken(token, true);
        var jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        var expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var username = signedJWT.getJWTClaimsSet().getSubject();
        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(jwtId)
                .expireAt(expirationTime)
                .build();
        invalidateTokenRepository.save(invalidateToken);
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return AuthenticationResponse.builder()
                .token(generateToken(user))
                .authenticated(true)
                .build();
    }

    //
    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {

        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        Date refreshTime = new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESH_DURATION, ChronoUnit.SECONDS).toEpochMilli());
        Date valiDate = isRefresh ? refreshTime : expirationTime;

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        boolean isValid = signedJWT.verify(verifier);

        if (!(isValid && new Date().before(valiDate))) {
            throw new AppException(ErrorCode.UNAUTHENTICATE);
        }
        if (invalidateTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATE);
        }
        return signedJWT;
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("http://localhost:8080")
                .issueTime(new Date())
                .claim("scope", scopeBuilder(user))
                .expirationTime(new Date(
                        Instant.now().plus(EXPIRATION_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
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

    private String scopeBuilder(User user) {
        StringJoiner scope = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            for (String role : user.getRoles()) {
                scope.add(role);
            }
        }
        return scope.toString().trim();
    }
}
