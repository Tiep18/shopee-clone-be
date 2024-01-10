package com.pt.jwt;

import com.pt.service.implement.AccountServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;

    public JwtAuthenticationFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtProvider.getTokenFromRequest(request);
            if (token != null && jwtProvider.validateAccessToken(token)) {
                String username = jwtProvider.extractUsername(token);
                UserDetails user = accountService.loadUserByUsername(username);
                if (user != null) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            if (Objects.equals(request.getServletPath(), "/refresh-access-token")
                    && ex instanceof ExpiredJwtException) {
                filterChain.doFilter(request, response);
                return;
            }
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
