package com.example.demo.controller;

import com.example.demo.dao.ForgotPasswordRepository;
import com.example.demo.dao.RoleRepository;
import com.example.demo.dao.UserMasterRepository;
import com.example.demo.entities.ForgotPassword;
import com.example.demo.entities.Role;
import com.example.demo.entities.UserMaster;
import com.example.demo.entities.request.LoginRequest;
import com.example.demo.entities.request.SignUpRequest;
import com.example.demo.entities.response.JwtResponse;
import com.example.demo.entities.response.MessageResponse;
import com.example.demo.service.EmsService;
import com.example.demo.service.impl.UserDetailsImpl;
import com.example.demo.service.jwt.JwtUtils;
import com.example.demo.utils.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserMasterRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    EmsService emsService;

    @Autowired
    ForgotPasswordRepository forgotPasswordRepository;

    @Value("${server.app-url}")
    private String appUrl;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getJwtPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        Optional<UserMaster> user = userRepository.findById(userDetails.getUsername());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername(),
                roles, user.get().getFirstName() , user.get().getLastName(),user.get().getDeployment()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        Optional<UserMaster> user = userRepository.findById(signUpRequest.getUserName());
        if (user.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already taken!"));
        }

        UserMaster newUser = new UserMaster();

        Set<String> strRoles = signUpRequest.getRole();
         Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new RuntimeException("Error 1: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(r -> {
                switch (r) {
                    case "employer":
                      Role adminRole = roleRepository.findByName(ERole.ROLE_EMPLOYER)
                                .orElseThrow(() -> new RuntimeException("Error 2: Role is not found."));
                         roles.add(adminRole);

                        break;

                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Error 3: Role is not found."));
                        roles.add(userRole);
                        break;
                }
            });
        }
        newUser.setUserName(signUpRequest.getUserName());
        newUser.setFirstName(signUpRequest.getFirstName());
        newUser.setLastName(signUpRequest.getLastName());
        newUser.setPassword(signUpRequest.getPassword());
        newUser.setJwtPassword(encoder.encode(signUpRequest.getPassword()));
        newUser.setRoles(roles);
        userRepository.save(newUser);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<UserMaster> forgotpassword(@RequestParam(name = "email") String emailId) throws Exception
    {
        try {
            emsService.processForgotPassword(emailId, appUrl);
            return new ResponseEntity<UserMaster>(HttpStatus.CREATED);
        } catch (IllegalArgumentException exp) {
            return new ResponseEntity<UserMaster>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/validateResetPasswordToken")
    public boolean processResetPassword(@RequestParam String token) throws Exception {
        Optional<ForgotPassword> forgotPass = forgotPasswordRepository.findByToken(token);

        if (!forgotPass.isPresent())
            return false;
        else {
            long requestTime = forgotPass.get().getRequestTime().getTime();
            long currentTimestamp = System.currentTimeMillis();
            long difference = Math.abs(currentTimestamp - requestTime);

            if (difference > 10800000)
                throw new RuntimeException("Link is invalid");
            else
                return true;
        }
    }

    @PostMapping("/resetPassword")
    public @ResponseBody void changePassword(@RequestBody ForgotPassword forgotPassword,
                                             HttpServletResponse response) throws IllegalArgumentException {
        String token = forgotPassword.getToken();
        String password = forgotPassword.getPassword();
        Optional<ForgotPassword> forgotPass = forgotPasswordRepository.findByToken(token);

        String encryptPassword = encoder.encode(password);
        if (!forgotPass.isPresent()) {
            throw new IllegalArgumentException("Invalid Request");
        } else {
            long requestTime = forgotPass.get().getRequestTime().getTime();
            long currentTimestamp = System.currentTimeMillis();
            long difference = Math.abs(currentTimestamp - requestTime);

            if (difference > 10800000)
                throw new RuntimeException("Link is invalid");
            else {
                UserMaster user = forgotPass.get().getUserMaster();
                user.setPassword(password);
                user.setJwtPassword(encryptPassword);
                userRepository.save(user);
            }
        }
    }
}
