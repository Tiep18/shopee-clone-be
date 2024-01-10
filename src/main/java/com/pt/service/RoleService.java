package com.pt.service;

import com.pt.entity.ERole;
import com.pt.entity.Role;

import java.util.Optional;

public interface RoleService {
    public Optional<Role> findByRoleName(ERole roleName);
}
