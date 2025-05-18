package com.mikepn.bankingsystem.v1.services.impl;

import com.mikepn.bankingsystem.v1.dto.request.auth.UpdateUserDTO;
import com.mikepn.bankingsystem.v1.dto.request.user.CreateAdminDTO;
import com.mikepn.bankingsystem.v1.dto.request.user.UserResponseDTO;
import com.mikepn.bankingsystem.v1.dto.request.user.UserRoleModificationDTO;
import com.mikepn.bankingsystem.v1.enums.ERole;
import com.mikepn.bankingsystem.v1.exceptions.AppException;
import com.mikepn.bankingsystem.v1.exceptions.BadRequestException;
import com.mikepn.bankingsystem.v1.exceptions.NotFoundException;
import com.mikepn.bankingsystem.v1.models.Role;
import com.mikepn.bankingsystem.v1.models.User;
import com.mikepn.bankingsystem.v1.repositories.IRoleRepository;
import com.mikepn.bankingsystem.v1.repositories.IUserRepository;
import com.mikepn.bankingsystem.v1.services.IRoleService;
import com.mikepn.bankingsystem.v1.services.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.security.admin.create.code}")
    private String adminCreateCode;

    public boolean isUserPresent(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    @Override
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with id: " + userId));
    }

    @Override
    public User getLoggedInUser() {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = userRepository.findUserByEmail(username).orElseThrow(() -> new NotFoundException("User Not Found"));
        user.setFullName(user.getFirstName() + " " + user.getLastName());
        return user;
    }

    public User createUserEntity(CreateAdminDTO createAdminDTO) {
        if (isUserPresent(createAdminDTO.getEmail())) {
            throw new BadRequestException("User with the email already exists");
        }

        return User.builder()
                .email(createAdminDTO.getEmail())
                .firstName(createAdminDTO.getFirstName())
                .lastName(createAdminDTO.getLastName())
                .fullName(createAdminDTO.getFirstName() + " " + createAdminDTO.getLastName())
                .dob(createAdminDTO.getDateOfBirth())
                .phoneNumber(createAdminDTO.getPhoneNumber())
                .password(passwordEncoder.encode(createAdminDTO.getPassword()))
                .roles(new HashSet<>(Collections.singletonList(roleService.getRoleByName(ERole.ADMIN))))
                .build();
    }

    @Override
    @Transactional
    public UserResponseDTO createAdmin(CreateAdminDTO createAdminDTO) {
        if (!createAdminDTO.getAdminCreateCode().equals(adminCreateCode)) {
            throw new BadRequestException("Invalid admin creation code");
        }

        User user = createUserEntity(createAdminDTO);
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserResponseDTO getUserById(UUID userId) {
        User user = findUserById(userId);
        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(UUID userId, UpdateUserDTO updateUserDTO) {
        User user = findUserById(userId);

        user.setFirstName(updateUserDTO.getFirstName());
        user.setLastName(updateUserDTO.getLastName());
        user.setFullName(updateUserDTO.getFirstName() + " " + updateUserDTO.getLastName());
        user.setPhoneNumber(updateUserDTO.getPhoneNumber());
        user.setEmail(updateUserDTO.getEmail());

        userRepository.save(user);

        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO addRoles(UUID userId, UserRoleModificationDTO userRoleModificationDTO) {
        User user = findUserById(userId);
        Set<Role> roles = user.getRoles();
        for (UUID roleId : userRoleModificationDTO.getRoles()) {
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Role not found"));
            roles.add(role);
        }


        user.getRoles().addAll(roles);
        userRepository.save(user);

        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO removeRoles(UUID userId, UserRoleModificationDTO userRoleModificationDTO) {
        User user = findUserById(userId);
        Set<Role> roles = user.getRoles();
        for (UUID roleId : userRoleModificationDTO.getRoles()) {
            Role role = roleRepository.findById(roleId).orElseThrow(() -> new NotFoundException("Role not found"));
            roles.add(role);
        }

        user.getRoles().removeAll(roles);
        userRepository.save(user);

        return new UserResponseDTO(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("User not found");
        }
        userRepository.deleteById(userId);
    }
}
