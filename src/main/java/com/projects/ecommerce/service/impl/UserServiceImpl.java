package com.projects.ecommerce.service.impl;

import com.projects.ecommerce.config.AppConfig;
import com.projects.ecommerce.dto.AddressDto;
import com.projects.ecommerce.dto.UserDto;
import com.projects.ecommerce.dto.request.AddAddressRequest;
import com.projects.ecommerce.dto.request.LoginRequest;
import com.projects.ecommerce.entity.Address;
import com.projects.ecommerce.entity.User;
import com.projects.ecommerce.enums.AuthProviderType;
import com.projects.ecommerce.enums.RoleType;
import com.projects.ecommerce.repository.AddressRepository;
import com.projects.ecommerce.repository.CategoryRepository;
import com.projects.ecommerce.repository.ProductRepository;
import com.projects.ecommerce.repository.UserRepository;
import com.projects.ecommerce.security.AuthUtil;
import com.projects.ecommerce.service.UserService;
import com.projects.ecommerce.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;
    private final AppConfig appConfig;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AddressRepository addressRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;


    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.findByEmailOrMobile(userDto.getEmail(), userDto.getMobile())
                .orElse(null);

        if (user != null) throw new IllegalArgumentException("user already found");

        user = modelMapper.map(userDto, User.class);

//        String identifier = userDto.getEmail();
//        if (identifier.contains("@")) {
//            user.setEmail(identifier);
//        } else {
//            user.setMobile(identifier);
//        }
        user.setMobile(userDto.getMobile());

        // Encrypt password
        user.setPassword(appConfig.passwordEncoder().encode(userDto.getPassword()));

        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            user.setRoles(userDto.getRoles());
        } else {
            user.setRoles(Set.of(RoleType.USER)); // default role
        }

        if (user.getAddresses() != null) {
            for (Address address : user.getAddresses()) {
                address.setUser(user);
            }
        }

        System.out.println("incomming addresses" + userDto.getAddresses());
        List<Address> addresses = new ArrayList<>();
        for (AddressDto aDto : userDto.getAddresses()) {
            Address addr = modelMapper.map(aDto, Address.class);
            addr.setUser(user);
            addresses.add(addr);
        }
        user.setAddresses(addresses);

        // TODO: Save the user in DB using service/repo
        userRepository.save(user);

        // Convert User to UserDto (response)
        UserDto response = modelMapper.map(user, UserDto.class);

        String token = authUtil.generateToken(user);
        response.setToken(token);

        return response;
    }

//    void addAdmin() {
//        UserDto user = new UserDto();
//        user.setId(1L);
//        user.setEmail("admin@gmail.com");
//        user.setPassword("admin123"); // In real apps, always hash passwords!
//        user.setName("John Doe");
//        user.setAddress("123, Main Street");
//        user.setCity("Mumbai");
//        user.setState("Maharashtra");
//        user.setPincode("400001");
//        user.setRoles(Set.of(RoleType.ADMIN));
//        user.setActive(true);
//    }

    @Override
    public UserDto login(LoginRequest dto) {

        User user = userRepository
                .findByEmailOrMobile(dto.getIdentifier(), dto.getIdentifier())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + dto.getIdentifier()));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getIdentifier(), dto.getPassword())
            );

            // Step 3: Retrieve authenticated principal
            User authenticatedUser = (User) authentication.getPrincipal();

            UserDto userDto = modelMapper.map(authenticatedUser, UserDto.class);

            String token = authUtil.generateToken(authenticatedUser);
            userDto.setToken(token);

            return userDto;
        } catch (BadCredentialsException ex) {
            // Specific exception for wrong password
            throw new RuntimeException("Invalid credentials, please check your username and password", ex);
        } catch (DisabledException ex) {
            // User account is disabled
            throw new RuntimeException("Account is disabled. Contact support.", ex);
        } catch (LockedException ex) {
            // User account is locked
            throw new RuntimeException("Account is locked. Contact support.", ex);
        } catch (AuthenticationException ex) {
            // Any other authentication exceptions
            throw new RuntimeException("Authentication failed", ex);
        } catch (Exception ex) {
            // Fallback for other unexpected errors
            throw new RuntimeException("An unexpected error occurred during login", ex);
        }
    }

    @Transactional(readOnly = true)
    public UserDto me(Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();

        User user = userRepository.findByIdWithAddresses(userId)
                .orElseThrow();

        return modelMapper.map(user, UserDto.class);
    }

    public AddressDto addAddresses(AddAddressRequest request) {
        User user = getCurrentUser();

        Address address = modelMapper.map(request, Address.class);
        address.setUser(user);

        Address saved = addressRepository.save(address);
        return modelMapper.map(saved, AddressDto.class);
    }


    @Transactional
    public ResponseEntity<UserDto> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        // fetch providerType and providerId
        // saved the providerType and providerId into the user
        // if the user has an account -> LOGIN
        // otherwise, first signup and then login
        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);

        User user = userRepository.findByProviderIdAndProviderType(providerId, providerType).orElse(null);

        String email = oAuth2User.getAttribute("email");

        User emailUser = (User) userRepository.findByEmail(email).orElse(null);

        if (user == null && emailUser == null) {
            // signUp flow:
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);

            UserDto newUser = new UserDto();
            newUser.setEmail(username);
            UserDto dto = createUser(newUser);
        } else if (user != null) {
            if (email != null && !email.isBlank()) {
                user.setEmail(email);
                userRepository.save(user);
            }
        } else {
            throw new BadCredentialsException("This email is already registered with provider " + emailUser.getProviderType());
        }

        UserDto userDto = modelMapper.map(user, UserDto.class);
        assert user != null;
        String token = authUtil.generateToken(user);
        userDto.setToken(token);
        return ResponseEntity.ok(userDto);
    }

//    @Override
//    public String upload(MultipartFile file) {
//        try {
//            String uploadDir = "/uploads/images/";
//            File dir = new File(uploadDir);
//            if (!dir.exists()) dir.mkdirs();
//
//            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//            Path path = Paths.get(uploadDir + filename);
//
//            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//
//            return "/uploads/images/" + filename; // RETURN URL PATH
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to upload image", e);
//        }
//    }

    @Override
    public void deleteFile(String filePath) {
        try {
            // Normalize path: remove leading slash
            if (filePath.startsWith("/")) {
                filePath = filePath.substring(1);
            }

            Path path = Paths.get(filePath);

            if (Files.exists(path)) {
                Files.delete(path);
            } else {
                throw new RuntimeException("File not found: " + filePath);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }

    @Override
    public List<AddressDto> getAddresses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return user.getAddresses()
                .stream().map(address -> {
                    AddressDto dto = modelMapper.map(address, AddressDto.class);
                    dto.setUserId(address.getUser().getId());
                    return dto;
                }).toList();
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication user: " + auth);
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            // Handle guest: return null, create temporary cart, or throw business exception
            throw new RuntimeException("User must be logged in to add to cart");
        }
        String identifier = auth.getName();
        return userRepository.findByEmailOrMobile(identifier, identifier)
                .orElseThrow(() -> new RuntimeException("User not found: " + identifier));
    }

    @Override
    public Long getAllUsers() {
        return userRepository.count();
    }

    public Page<UserDto> getUsers(String search, Boolean active, Pageable pageable) {
        try {
            Page<User> usersPage = userRepository.findAll(
                    UserSpecification.combine(search, active),
                    pageable
            );
            return usersPage.map(user -> modelMapper.map(user, UserDto.class));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto setUserActive(Long userId, boolean active) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Rule 1: Prevent deactivating the ONLY ADMIN
        boolean isAdmin = user.getRoles() != null
                && user.getRoles().contains(RoleType.ADMIN);

        if (isAdmin && !active) {
            long activeAdminCount =
                    userRepository.countByRolesContainingAndActive(RoleType.ADMIN, true);

            if (activeAdminCount <= 1) {
                throw new IllegalStateException(
                        "System must contain at least one active ADMIN"
                );
            }
        }

        user.setActive(active);

        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserDto.class);
    }


    public List<String> suggestKeywords(String q, int limit) {
        if (q == null || q.isBlank() || limit <= 0) {
            return Collections.emptyList();
        }

        String query = q.trim().toLowerCase();
        int fetchSize = Math.max(limit, 3);

        LinkedHashSet<String> result = new LinkedHashSet<>(limit);

        add(result, productRepository.suggestProductNames(query, fetchSize), limit);
        add(result, productRepository.suggestProductDescriptionSnippets(query, fetchSize), limit);
        add(result, categoryRepository.suggestCategoryNamesOrDescriptions(query, fetchSize), limit);

        return new ArrayList<>(result);
    }


    private void add(Set<String> target, List<String> source, int limit) {
        if (source == null) return;

        for (String s : source) {
            if (s == null || s.isBlank()) continue;

            target.add(s.trim());
            if (target.size() >= limit) break;
        }
    }
}
