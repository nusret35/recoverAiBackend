package com.kizilaslan.recoverAiBackend.exception;

import com.kizilaslan.recoverAiBackend.model.ErrorLog;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.repository.ErrorRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private ErrorRepository errorRepository;

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        String paramType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown";
        String message = "Invalid value for parameter '" + paramName + "'. Expected type: " + paramType;
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body("Invalid request");
    }

    @ExceptionHandler({ UserAlreadyExistsException.class })
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({ UserAddictionNotFoundException.class })
    public ResponseEntity<Object> handleUserAddictionNotFoundException(UserAddictionNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({ UserAddictionAlreadyExistsException.class })
    public ResponseEntity<Object> handleUserAddictionAlreadyExistsException(
            UserAddictionAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({ AddictionNotFoundException.class })
    public ResponseEntity<Object> handleAddictionNotFoundException(AddictionNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({ UserGoalAlreadyExists.class })
    public ResponseEntity<Object> handleUserGoalAlreadyExists(UserGoalAlreadyExists exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler({ UserGoalNotFound.class })
    public ResponseEntity<Object> handleUserGoalNotFound(UserGoalNotFound exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler({ AuthenticationException.class })
    public ResponseEntity<Object> handleAuthenticationFailedException(AuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }

    @ExceptionHandler({ IOException.class })
    public void handleIOException(IOException exception) {
        System.out.println(exception.getMessage());
        // TODO: Report bug
    }

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ErrorLog errorLog = new ErrorLog(user.getId(), exception.getMessage());
        errorRepository.save(errorLog);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @ExceptionHandler({ GoogleSignInFailedException.class })
    public ResponseEntity<Object> handleGoogleSignInFailedException(GoogleSignInFailedException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
