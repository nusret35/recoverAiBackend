package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.exception.UserAlreadyExistsException;
import com.kizilaslan.recoverAiBackend.exception.UserNotFoundException;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserAddiction;
import com.kizilaslan.recoverAiBackend.repository.UserRepository;
import com.kizilaslan.recoverAiBackend.util.ValidationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserAddictionService userAddictionService;

    public List<AppUser> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public AppUser findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with given ID"));
    }

    public AppUser create(AppUser user) {
        boolean userExistsByEmail = user.getEmail() != null & userRepository.existsByEmail(user.getEmail());
        boolean userExistsByPhoneNumber = user.getPhoneNumber() != null
                & userRepository.existsByPhoneNumber(user.getPhoneNumber());
        if (userExistsByEmail || userExistsByPhoneNumber) {
            throw new UserAlreadyExistsException("User already exists");
        }
        return userRepository.save(user);
    }

    public AppUser update(AppUser user) {
        Optional<AppUser> existingUser = userRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new UserNotFoundException("User not found with given ID");
        }
        return userRepository.save(user);
    }

    public void delete() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        List<UserAddiction> userAddictions = userAddictionService.findAllByUserId(user.getId());
        userAddictions.forEach(addiction -> {
            userAddictionService.deleteById(addiction.getId().getAddiction());
        });
        userRepository.delete(user);
    }

    public void deleteById(UUID id) {
        Optional<AppUser> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with given ID");
        }
        userRepository.deleteById(id);
    }

    public AppUser findByUsername(String username) {
        if (ValidationUtils.isValidEmail(username)) {
            AppUser user = userRepository.findByEmail(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with given identifier");
            }
            return user;
        }

        return userRepository.findByPhoneNumber(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByEmail(username) || userRepository.existsByPhoneNumber(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with given identifier");
        }
        return user;
    }
}
