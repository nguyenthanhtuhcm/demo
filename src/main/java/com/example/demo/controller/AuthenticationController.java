package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.request.LogoutRequest;
import com.example.demo.dto.request.RefreshRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.example.demo.dto.response.LogoutResponse;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {

        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticate(request))
                .build();
    }
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {

        return ApiResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();
    }
    @PostMapping("/refreshToken")
    public ApiResponse<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {

        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.refreshToken(request))
                .build();
    }
    @PostMapping("/logout")
    public ApiResponse<LogoutResponse> logout(@RequestBody LogoutRequest request) {
        boolean logout = false;
        try
        {
            authenticationService.logout(request);
            logout = true;
        }
        catch (Exception e)
        {
            throw new AppException(ErrorCode.UNAUTHENTICATE);
        }
       return ApiResponse.<LogoutResponse>builder()
                .result(LogoutResponse.builder()
                        .valid(logout)
                        .build())
                .build();
    }
}
