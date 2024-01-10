package com.pt.security;

import com.pt.entity.Account;
import com.pt.entity.RefreshToken;
import com.pt.exception.NotFoundException;
import com.pt.jwt.JwtProvider;
import com.pt.repository.AccountRepository;
import com.pt.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomLogoutHandler implements LogoutHandler {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    JwtProvider jwtProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = jwtProvider.getTokenFromRequest(request);
        if (token != null && jwtProvider.validateAccessToken(token)) {
            String email = jwtProvider.extractUsername(token);
            Account account = accountRepository.findByEmail(email);
            Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccount_Id(account.getId());
            if (refreshToken.isEmpty()) throw new NotFoundException("Refresh token not found in the database");
            refreshTokenRepository.delete(refreshToken.get());

            response.setStatus(HttpStatus.OK.value());
            try {
                response.getWriter().write("Logout successfully");
                response.getWriter().flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
