package com.pt.controller;

import com.pt.dto.req.LoginReqDTO;
import com.pt.dto.req.RefreshTokenReqDTO;
import com.pt.dto.req.RegisterReqDto;
import com.pt.dto.res.LoginResDTO;
import com.pt.dto.res.RefreshTokenResDTO;
import com.pt.dto.res.ResponseDTO;
import com.pt.entity.Account;
import com.pt.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(value = "*")
@RequestMapping
public class AuthController {
    @Autowired
    private AccountService accountService;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@Valid @RequestBody LoginReqDTO loginReqDTO) throws Exception {
        LoginResDTO loginResDTO = accountService.login(loginReqDTO);
        return ResponseEntity.ok(new ResponseDTO("Login successfully", loginResDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody RegisterReqDto registerReqDto) throws Exception {
        Account account = accountService.register(registerReqDto);
        return ResponseEntity.ok(new ResponseDTO("Create account successfully", account));
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<ResponseDTO> refreshAccessToken(@RequestBody RefreshTokenReqDTO refreshTokenReqDTO) throws Exception {
        RefreshTokenResDTO refreshTokenResDTO = accountService.refreshToken(refreshTokenReqDTO);
        return ResponseEntity.ok(new ResponseDTO("Refresh token successfully", refreshTokenResDTO));
    }

//    @PostMapping("/logout")
//    public ResponseEntity<ResponseDTO> logout(Authentication authentication) {
//        System.out.println("logout");
//        accountService.logout(authentication);
//        return ResponseEntity.ok(new ResponseDTO("Logout successfully"));
//    }
}
