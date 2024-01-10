package com.pt.service.implement;

import com.pt.dto.req.LoginReqDTO;
import com.pt.dto.req.RefreshTokenReqDTO;
import com.pt.dto.req.RegisterReqDto;
import com.pt.dto.req.UpdateUserReqDTO;
import com.pt.dto.res.LoginResDTO;
import com.pt.dto.res.RefreshTokenResDTO;
import com.pt.entity.Account;
import com.pt.entity.ERole;
import com.pt.entity.RefreshToken;
import com.pt.entity.Role;
import com.pt.exception.NotFoundException;
import com.pt.exception.PasswordInvalidException;
import com.pt.jwt.JwtProvider;
import com.pt.repository.AccountRepository;
import com.pt.repository.RefreshTokenRepository;
import com.pt.repository.RoleRepository;
import com.pt.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private RefreshTokenRepository refreshTokenRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    AuthenticationManager authenticationManager;

    @Override
    public Account register(RegisterReqDto registerReqDto) throws Exception {

        // check if username already exists
        Account foundAccount = accountRepo.findByEmail(registerReqDto.getEmail());
        if (foundAccount != null) {
            throw new Exception("Email already exists");
        }
        Account account = new Account();
        LocalDateTime date = LocalDateTime.now();

        // add role to account
        Set<Role> roles = new HashSet<>();
        if (registerReqDto.getRoles() == null || registerReqDto.getRoles().isEmpty()) {
            Role role = roleRepo.findByRoleName(ERole.USER).orElseThrow();
            roles.add(role);
        } else {
            registerReqDto.getRoles().forEach(role -> {
                switch (role) {
                    case "USER":
                        Role uRole = roleRepo.findByRoleName(ERole.USER).orElseThrow();
                        roles.add(uRole);
                    case "ADMIN":
                        Role adRole = roleRepo.findByRoleName(ERole.ADMIN).orElseThrow();
                        roles.add(adRole);
                }
            });
        }

        // hash password
        String password = passwordEncoder.encode(registerReqDto.getPassword());
        account.setPassword(password);

        // save account
        account.setEmail(registerReqDto.getEmail());
        account.setPassword(password);
        account.setCreatedAt(date);
        account.setUpdatedAt(date);
        account.setRoles(roles);
        return accountRepo.save(account);
    }

    @Override
    @Transactional
    public LoginResDTO login(LoginReqDTO loginReq) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginReq.getEmail(), loginReq.getPassword()));

        // Generate token
        String accessToken = jwtProvider.generateAtToken(authentication);
        String refreshToken = jwtProvider.generateRtToken(authentication);
        Account account = accountRepo.findByEmail(loginReq.getEmail());

        // Save refresh token
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepo.findByAccount_Id(account.getId());
        RefreshToken refreshTokenToDb = refreshTokenOpt.orElseGet(RefreshToken::new);
        refreshTokenToDb.setAccount(account);
        refreshTokenToDb.setToken(refreshToken);
        refreshTokenRepo.save(refreshTokenToDb);
        return new LoginResDTO(accessToken, refreshToken, account);
    }

    @Override
    public Account findByEmail(String email) {
        return accountRepo.findByEmail(email);
    }

    @Override
    public RefreshTokenResDTO refreshToken(RefreshTokenReqDTO refreshTokenReqDTO) throws Exception {
        String token = refreshTokenReqDTO.getRefreshToken();
        if (token != null && jwtProvider.validateRefreshToken(token)) {
            Optional<RefreshToken> refreshTokenDB = refreshTokenRepo.findByToken(token);
            if (refreshTokenDB.isPresent()) {
                Account account = refreshTokenDB.get().getAccount();
                List<GrantedAuthority> authorities = account.getRoles().stream().map(role ->
                        new SimpleGrantedAuthority(role.getRoleName().name())
                ).collect(Collectors.toList());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(account.getEmail(), null, authorities);
                String accessToken = jwtProvider.generateRtToken(authentication);
                return new RefreshTokenResDTO(accessToken);
            }
            return null;
        } else {
            throw new Exception("Token not found");
        }
    }

    @Override
    public Account updateUser(UpdateUserReqDTO updateUser, Account account) {
        String password = updateUser.getPassword();
        if (password != null) {
            if (passwordEncoder.matches(password, account.getPassword())) {
                account.setPassword(passwordEncoder.encode(updateUser.getNew_password()));
                return accountRepo.save(account);
            } else {
                throw new PasswordInvalidException("Password is invalid");
            }
        }
        account.setAddress(updateUser.getAddress());
        account.setName(updateUser.getName());
        account.setPhone(updateUser.getPhone());
        account.setDateOfBirth(updateUser.getDate_of_birth());
        account.setUpdatedAt(LocalDateTime.now());
        return accountRepo.save(account);
    }

    @Override
    public void logout(Authentication authentication) {
        Account account = accountRepo.findByEmail(authentication.getName());
        Optional<RefreshToken> refreshToken = refreshTokenRepo.findByAccount_Id(account.getId());
        if (refreshToken.isEmpty()) throw new NotFoundException("Refresh token not found in database");
        refreshTokenRepo.delete(refreshToken.get());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepo.findByEmail(email);
        if (account == null)
            throw new UsernameNotFoundException("User not found with email: " + email);
        List<GrantedAuthority> authorities = account.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getRoleName().name())
        ).collect(Collectors.toList());
        ;
        return new User(email, account.getPassword(), authorities);
    }
}
