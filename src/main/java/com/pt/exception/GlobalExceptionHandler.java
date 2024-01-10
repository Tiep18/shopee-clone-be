package com.pt.exception;

import com.pt.dto.res.ErrorDTO;
import com.pt.dto.res.ResponseDTO;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorDTO> handleJwtException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorDTO> handleUserNamePasswordException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO("Username or password is incorrect"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleAccessDeniedException(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorDTO(ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> handleAuthenticationException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDTO(ex.getMessage()));
    }

    // handle validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ResponseDTO("Error", errors));
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorDTO> handleTypeMismatch(TypeMismatchException ex) {
        System.out.println(ex.getPropertyName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDTO(ex.getPropertyName() + " is invalid"));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorDTO(ex.getMessage())
        );
    }

    @ExceptionHandler(PasswordInvalidException.class)
    public ResponseEntity<ResponseDTO> handlePasswordInvalidException(PasswordInvalidException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("password", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                new ResponseDTO("Error", error)
        );
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ResponseDTO> handleOtherException(Exception ex) {
//        System.out.println("----------lot vao day");
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new ResponseDTO(ex.getMessage()));
//    }

}
