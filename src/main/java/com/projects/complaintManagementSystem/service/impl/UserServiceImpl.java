package com.projects.complaintManagementSystem.service.impl;

import com.projects.complaintManagementSystem.config.AppConfig;
import com.projects.complaintManagementSystem.dto.AddressDto;
import com.projects.complaintManagementSystem.dto.UserDto;
import com.projects.complaintManagementSystem.dto.request.CreateUserDto;
import com.projects.complaintManagementSystem.dto.request.LoginRequest;
import com.projects.complaintManagementSystem.entity.Address;
import com.projects.complaintManagementSystem.entity.User;
import com.projects.complaintManagementSystem.enums.AccountStatusType;
import com.projects.complaintManagementSystem.enums.RoleType;
import com.projects.complaintManagementSystem.repository.AddressRepository;
import com.projects.complaintManagementSystem.repository.UserRepository;
import com.projects.complaintManagementSystem.security.AuthUtil;
import com.projects.complaintManagementSystem.service.UserService;
import com.projects.complaintManagementSystem.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final AuthUtil authUtil;
    private final AppConfig appConfig;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByMobile(createUserDto.getMobile())) {
            throw new RuntimeException("Mobile number already exists");
        }

        User user = modelMapper.map(createUserDto, User.class);
        user.setRoles(Set.of(RoleType.CITIZEN
        )); // default role
        user.setAccountStatusType(AccountStatusType.PENDING);

        user.setMobile(normalizeMobile(createUserDto.getMobile()));

        // Encrypt password
        user.setPassword(appConfig.passwordEncoder().encode(createUserDto.getPassword()));

        List<Address> addresses = new ArrayList<>();
        for (AddressDto aDto : createUserDto.getAddresses()) {
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

    @Override
    public UserDto login(LoginRequest dto) {

        String identifier;
        if (dto.getIdentifier().contains("@")) {
            identifier = dto.getIdentifier();
        } else {
            identifier = normalizeMobile(dto.getIdentifier());
        }


        User user = userRepository
                .findByEmailOrMobile(identifier, identifier)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found with identifier: " + identifier));
        
//        if (user.getAccountStatusType() != AccountStatusType.ACTIVE) {
//            throw new RuntimeException("Account not verified. Please verify mobile/email.");
//        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(identifier, dto.getPassword()));

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
    @Override
    public UserDto me(Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();

        User user = userRepository.findByIdWithAddresses(userId)
                .orElseThrow();

        return modelMapper.map(user, UserDto.class);
    }

    @Override
    @Transactional
    public AddressDto addAddresses(AddressDto request) {
        User user = getCurrentUser();

        try {
            Address address = modelMapper.map(request, Address.class);
            address.setUser(user);

            Address saved = addressRepository.save(address);
            return modelMapper.map(saved, AddressDto.class);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occur during adding the address" + e);
        }
    }

    // @Override
    // @Transactional
    // public ResponseEntity<UserDto> handleOAuth2LoginRequest(OAuth2User
    // oAuth2User, String registrationId) {
    // // fetch providerType and providerId
    // // saved the providerType and providerId into the user
    // // if the user has an account -> LOGIN
    // // otherwise, first signup and then login
    // AuthProviderType providerType =
    // authUtil.getProviderTypeFromRegistrationId(registrationId);
    // String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User,
    // registrationId);
    //
    // User user = userRepository.findByProviderIdAndProviderType(providerId,
    // providerType).orElse(null);
    //
    // String email = oAuth2User.getAttribute("email");
    //
    // User emailUser = (User) userRepository.findByEmail(email).orElse(null);
    //
    // if (user == null && emailUser == null) {
    // // signUp flow:
    // String username = authUtil.determineUsernameFromOAuth2User(oAuth2User,
    // registrationId, providerId);
    //
    // UserDto newUser = new UserDto();
    // newUser.setEmail(username);
    // UserDto dto = createUser(newUser);
    // } else if (user != null) {
    // if (email != null && !email.isBlank()) {
    // user.setEmail(email);
    // userRepository.save(user);
    // }
    // } else {
    // throw new BadCredentialsException(
    // "This email is already registered with provider " +
    // emailUser.getProviderType());
    // }
    //
    // UserDto userDto = modelMapper.map(user, UserDto.class);
    // assert user != null;
    // String token = authUtil.generateToken(user);
    // userDto.setToken(token);
    // return ResponseEntity.ok(userDto);
    // }

    // @Override
    // public String upload(MultipartFile file) {
    // try {
    // String uploadDir = "/uploads/images/";
    // File dir = new File(uploadDir);
    // if (!dir.exists()) dir.mkdirs();
    //
    // String filename = System.currentTimeMillis() + "_" +
    // file.getOriginalFilename();
    // Path path = Paths.get(uploadDir + filename);
    //
    // Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    //
    // return "/uploads/images/" + filename; // RETURN URL PATH
    // } catch (IOException e) {
    // throw new RuntimeException("Failed to upload image", e);
    // }
    // }

    // @Override
    // public void deleteFile(String filePath) {
    // try {
    // // Normalize path: remove leading slash
    // if (filePath.startsWith("/")) {
    // filePath = filePath.substring(1);
    // }
    //
    // Path path = Paths.get(filePath);
    //
    // if (Files.exists(path)) {
    // Files.delete(path);
    // } else {
    // throw new RuntimeException("File not found: " + filePath);
    // }
    //
    // } catch (IOException e) {
    // throw new RuntimeException("Failed to delete file: " + filePath, e);
    // }
    // }

    @Transactional(readOnly = true)
    @Override
    public List<AddressDto> getAddresses() {
        User user = getCurrentUser();

        return user.getAddresses()
                .stream().map(address -> {
                    return modelMapper.map(address, AddressDto.class);
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
                    pageable);
            return usersPage.map(user -> modelMapper.map(user, UserDto.class));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return modelMapper.map(user, UserDto.class);
    }

    private void add(Set<String> target, List<String> source, int limit) {
        if (source == null)
            return;

        for (String s : source) {
            if (s == null || s.isBlank())
                continue;

            target.add(s.trim());
            if (target.size() >= limit)
                break;
        }
    }

    public String normalizeMobile(String mobile) {
        if (mobile == null) return null;

        mobile = mobile.trim().replaceAll("\\s+", "");

        // remove leading +
        if (mobile.startsWith("+")) {
            mobile = mobile.substring(1);
        }

        // remove leading 0
        if (mobile.startsWith("0")) {
            mobile = mobile.substring(1);
        }

        // if already starts with 91
        if (mobile.startsWith("91")) {
            return "+" + mobile;
        }

        // otherwise assume Indian number
        return "+91" + mobile;
    }
//    public String uploadImage(@Valid MultipartFile image) {
//        CloudinaryUploadResult result = cloudService.upload(image);
//        return result.getImage();
//    }
}
