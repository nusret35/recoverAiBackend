package com.kizilaslan.recoverAiBackend.response;

import com.kizilaslan.recoverAiBackend.model.AppUser;
import lombok.Data;

import java.util.Map;

@Data
public class GoogleSignInResponse {

    private final boolean isExistingUser;

    private final Map<String, String> profileDetails;

    private final String authToken;

    private GoogleSignInResponse(boolean isExistingUser, Map<String, String> profileDetails, String authToken) {
        this.isExistingUser = isExistingUser;
        this.profileDetails = profileDetails;
        this.authToken = authToken;
    }

    public static GoogleSignInResponse existingUser(AppUser user, String authToken) {
        return new GoogleSignInResponse(true, null, authToken);
    }

    public static GoogleSignInResponse newUser(Map<String, String> profileDetails) {
        return new GoogleSignInResponse(false, profileDetails, null);
    }

}
