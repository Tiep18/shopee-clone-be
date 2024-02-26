package com.pt.security.oauth;

import com.pt.entity.Account;
import com.pt.entity.ERole;
import com.pt.entity.RefreshToken;
import com.pt.entity.Role;
import com.pt.jwt.JwtProvider;
import com.pt.repository.AccountRepository;
import com.pt.repository.RefreshTokenRepository;
import com.pt.repository.RoleRepository;
import com.pt.service.AccountService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class Oauth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private String frontendBaseUrl;
    @Value("${frontend-base-url}")

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        this.setAlwaysUseDefaultTargetUrl(true);
        DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String avatar = (String) attributes.get("picture");
        try {
            Account account = accountService.findByEmail(email);
            if (account == null) {
                account = new Account();
                LocalDateTime date = LocalDateTime.now();
                Set<Role> roles = new HashSet<>();
                roles.add(roleRepo.findByRoleName(ERole.USER).orElseThrow());
                account.setEmail(email);
                account.setName(name);
                account.setAvatar(avatar);
                account.setCreatedAt(date);
                account.setUpdatedAt(date);
                account.setRoles(roles);
                account.setPassword(UUID.randomUUID().toString());
                accountRepository.save(account);
            }
            // Generate token
            String accessToken = jwtProvider.generateAtToken(email);
            String refreshToken = jwtProvider.generateRtToken(email);

            // Save refresh token
            Optional<RefreshToken> refreshTokenOpt = refreshTokenRepo.findByAccount_Id(account.getId());
            RefreshToken refreshTokenToDb = refreshTokenOpt.orElseGet(RefreshToken::new);
            refreshTokenToDb.setAccount(account);
            refreshTokenToDb.setToken(refreshToken);
            refreshTokenRepo.save(refreshTokenToDb);

            String url = frontendBaseUrl + "/oauth/redirect";
            String targetUrl = UriComponentsBuilder.fromUriString(url)
                    .queryParam("access_token", accessToken)
                    .queryParam("refresh_token", refreshToken)
                    .build().toUriString();
            this.setDefaultTargetUrl(targetUrl);
            super.onAuthenticationSuccess(request, response, authentication);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
