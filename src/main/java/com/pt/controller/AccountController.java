package com.pt.controller;

import com.pt.dto.req.UpdateUserReqDTO;
import com.pt.dto.res.ResponseDTO;
import com.pt.entity.Account;
import com.pt.jwt.JwtProvider;
import com.pt.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtProvider jwtProvider;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader HttpHeaders request) throws Exception {
        String token = jwtProvider.getTokenFromRequest(request);
        String email = jwtProvider.extractUsername(token);
        Account account = accountService.findByEmail(email);
        return ResponseEntity.ok(new ResponseDTO("Get account successfully", account));
    }

    @PutMapping("/user")
    public ResponseEntity<ResponseDTO> updateMe(@RequestBody @Valid UpdateUserReqDTO updateUser,
                                                Authentication authentication) throws Exception {
        Account account = accountService.findByEmail(authentication.getName());
        Account updatedAccount = accountService.updateUser(updateUser, account);
        return ResponseEntity.ok(new ResponseDTO("Update account successfully", updatedAccount));
    }
}
