package com.pt.service;

import com.pt.dto.req.LoginReqDTO;
import com.pt.dto.req.RefreshTokenReqDTO;
import com.pt.dto.req.RegisterReqDto;
import com.pt.dto.req.UpdateUserReqDTO;
import com.pt.dto.res.LoginResDTO;
import com.pt.dto.res.RefreshTokenResDTO;
import com.pt.entity.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface AccountService extends UserDetailsService {
    Account register(RegisterReqDto registerReqDto) throws Exception;

    LoginResDTO login(LoginReqDTO loginReqDTO) throws Exception;

    Account findByEmail(String email) throws Exception;

    RefreshTokenResDTO refreshToken(RefreshTokenReqDTO refreshTokenReqDTO) throws Exception;

    Account updateUser(UpdateUserReqDTO updateUser, Account account) throws NoSuchMethodException, MethodArgumentNotValidException;

    void logout(Authentication authentication);
}


