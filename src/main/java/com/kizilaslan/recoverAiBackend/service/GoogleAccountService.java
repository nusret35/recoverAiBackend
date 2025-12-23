package com.kizilaslan.recoverAiBackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kizilaslan.recoverAiBackend.exception.GoogleSignInFailedException;
import com.kizilaslan.recoverAiBackend.exception.UserNotFoundException;
import com.kizilaslan.recoverAiBackend.model.GoogleAccount;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.properties.GoogleProperties;
import com.kizilaslan.recoverAiBackend.repository.GoogleAccountRepository;
import com.kizilaslan.recoverAiBackend.request.RegisterUserWithGoogleRequest;
import com.kizilaslan.recoverAiBackend.response.AuthenticationResponse;
import com.kizilaslan.recoverAiBackend.response.GoogleSignInResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GoogleAccountService {

    private final GoogleProperties googleProperties;
    private final GoogleAccountRepository googleAccountRepository;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public void save(GoogleAccount googleAccount) {
        googleAccountRepository.save(googleAccount);
    }

    private Map<String, String> getOauthAccessTokenGoogle(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", googleProperties.getRedirectUri());
        params.add("client_id", googleProperties.getClientId());
        params.add("client_secret", googleProperties.getClientSecret());
        params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile");
        params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
        params.add("scope", "openid");
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);
        String url = "https://oauth2.googleapis.com/token";
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new GoogleSignInFailedException("OAuth token response cannot be parsed");
        }
    }

    private Map<String, String> getProfileDetailsGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                String responseBody = response.getBody();
                return objectMapper.readValue(responseBody, new TypeReference<Map<String, String>>() {
                });
            } catch (JsonProcessingException e) {
                throw new GoogleSignInFailedException("Google account info cannot be parsed");
            }
        }
        throw new GoogleSignInFailedException("Google account not found");
    }

    private GoogleSignInResponse authenticate(GoogleAccount googleAccount) {
        AppUser user = googleAccount.getUser();
        if (user == null) {
            throw new UserNotFoundException("User not found for Google account: " + googleAccount.getId());
        }
        String jwtToken = jwtService.generateToken(user);
        return GoogleSignInResponse.existingUser(user, jwtToken);
    }

    public GoogleSignInResponse googleSignIn(String code) {
        Map<String, String> result = getOauthAccessTokenGoogle(code);
        Map<String, String> profileDetail = getProfileDetailsGoogle(result.get("access_token"));
        String id = profileDetail.get("id");
        Optional<GoogleAccount> account = googleAccountRepository.findById(id);
        return account.map(this::authenticate).orElseGet(() -> GoogleSignInResponse.newUser(profileDetail));
    }

    @Transactional
    public AuthenticationResponse registerWithGoogle(RegisterUserWithGoogleRequest request) {
        AppUser newUser = AppUser.fromRegisterUserWithGoogleRequest(request);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        AppUser user = userService.create(newUser);
        String jwtToken = jwtService.generateToken(user);
        GoogleAccount googleAccount = new GoogleAccount(request.getGoogleId(), request.getName(), request.getSurname(),
                request.getUsername(), user);
        save(googleAccount);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

}
