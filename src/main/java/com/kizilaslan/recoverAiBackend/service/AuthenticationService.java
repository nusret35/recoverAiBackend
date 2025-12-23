package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.exception.UserAlreadyExistsException;
import com.kizilaslan.recoverAiBackend.model.Role;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.request.AuthenticationRequest;
import com.kizilaslan.recoverAiBackend.request.ChangePasswordRequest;
import com.kizilaslan.recoverAiBackend.request.RegisterUserRequest;
import com.kizilaslan.recoverAiBackend.request.RegisterUserWithAppleRequest;
import com.kizilaslan.recoverAiBackend.response.AuthenticationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public void checkUsernameForNewAccount(String username) {
        boolean userExists = userService.existsByUsername(username);
        if (userExists) {
            throw new UserAlreadyExistsException("User already exists");
        }
    }

    @Transactional
    public AuthenticationResponse register(RegisterUserRequest request) {
        AppUser newUser = AppUser.fromRegisterUserRequest(request);
        AppUser user = userService.create(newUser);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Transactional
    public AuthenticationResponse registerWithAppleRequest(RegisterUserWithAppleRequest request) {
        AppUser newUser = AppUser.fromRegisterUserWithAppleRequest(request);
        AppUser user = userService.create(newUser);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setRole(Role.USER);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        AppUser user = userService.findByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Transactional
    public AuthenticationResponse changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        AuthenticationRequest authRequest = new AuthenticationRequest(user.getUsername(), request.getCurrentPassword());
        authenticate(authRequest);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.update(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

}
