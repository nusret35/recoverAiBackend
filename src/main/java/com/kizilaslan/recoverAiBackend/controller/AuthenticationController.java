package com.kizilaslan.recoverAiBackend.controller;

import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.repository.UserRepository;
import com.kizilaslan.recoverAiBackend.request.*;
import com.kizilaslan.recoverAiBackend.response.AuthenticationResponse;
import com.kizilaslan.recoverAiBackend.response.GoogleSignInResponse;
import com.kizilaslan.recoverAiBackend.response.SignInWithAppleResponse;
import com.kizilaslan.recoverAiBackend.service.AuthenticationService;
import com.kizilaslan.recoverAiBackend.service.GoogleAccountService;
import com.kizilaslan.recoverAiBackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authService;
    private final GoogleAccountService googleService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/check-username-for-new-account")
    public ResponseEntity<?> checkUsernameForNewAccount(@RequestBody CheckUsernameForNewAccountRequest request) {
        authService.checkUsernameForNewAccount(request.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/register-with-apple")
    public ResponseEntity<AuthenticationResponse> registerWithApple(@RequestBody RegisterUserWithAppleRequest request) {
        return ResponseEntity.ok(authService.registerWithAppleRequest(request));
    }

    @PostMapping("/register-with-google")
    public ResponseEntity<AuthenticationResponse> registerWithGoogle(
            @RequestBody RegisterUserWithGoogleRequest request) {
        return ResponseEntity.ok(googleService.registerWithGoogle(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authService.authenticate(authenticationRequest));
    }

    @PostMapping("/login-with-apple")
    public ResponseEntity<?> appleLogin(@RequestBody AppleSignInRequest appleSignInRequest) {
        AppUser user = userRepository.findByAppleId(appleSignInRequest.getId());
        if (user != null) {
            String jwt = jwtService.generateToken(user);
            SignInWithAppleResponse response = new SignInWithAppleResponse(jwt);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/oauth2/code/google")
    public ResponseEntity<Void> googleLogin(@RequestParam("code") String code) {
        GoogleSignInResponse result = googleService.googleSignIn(code);
        HttpHeaders headers = new HttpHeaders();
        if (result.isExistingUser()) {
            String baseRedirectUrl = "vitaloop://sign-in";
            String authToken = URLEncoder.encode(result.getAuthToken(), StandardCharsets.UTF_8);
            String redirectUrl = String.format("%s?authToken=%s", baseRedirectUrl, authToken);
            headers.setLocation(URI.create(redirectUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
        String baseRedirectUrl = "vitaloop://create-account/profileInfo";
        Map<String, String> profileDetails = result.getProfileDetails();
        String email = URLEncoder.encode(profileDetails.get("email"), StandardCharsets.UTF_8);
        String name = URLEncoder.encode(profileDetails.get("given_name"), StandardCharsets.UTF_8);
        String surname = URLEncoder.encode(profileDetails.get("family_name"), StandardCharsets.UTF_8);
        String id = URLEncoder.encode(profileDetails.get("id"), StandardCharsets.UTF_8);
        String redirectUrl = String.format("%s?googleEmail=%s&name=%s&surname=%s&googleId=%s", baseRedirectUrl, email,
                name, surname, id);
        headers.setLocation(URI.create(redirectUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}