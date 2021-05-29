package erasmusupm.adiaz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import erasmusupm.adiaz.message.request.LoginForm;
import erasmusupm.adiaz.message.request.SignUpForm;
import erasmusupm.adiaz.message.response.JwtResponse;
import erasmusupm.adiaz.message.response.ResponseMessage;
import erasmusupm.adiaz.model.Patient;
import erasmusupm.adiaz.model.Role;
import erasmusupm.adiaz.model.RoleName;
import erasmusupm.adiaz.model.User;
import erasmusupm.adiaz.repository.PatientRepository;
import erasmusupm.adiaz.repository.RoleRepository;
import erasmusupm.adiaz.repository.UserRepository;
import erasmusupm.adiaz.security.jwt.JwtProvider;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/restApi/auth")
public class AuthRESTController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) ((org.springframework.security.core.Authentication) authentication).getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,userDetails.getUsername(), userDetails.getAuthorities()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken."), HttpStatus.BAD_REQUEST);
        }

        // Create user account
        User user = new User(signUpRequest.getUsername(), passwordEncoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        strRoles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Fail -> Cause: Admin Role not found."));
                    roles.add(adminRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Fail -> Cause: User Role not found."));
                    roles.add(userRole);
                    Patient patient = new Patient();
                    patient.setFirstname(signUpRequest.getFirstname());
                    patient.setLastname(signUpRequest.getLastname());
                    patient.setEmail(signUpRequest.getEmail());
                    patient.setTelephone(signUpRequest.getTelephone());
                    patient.setGender(signUpRequest.getGender());
                    user.setPatient(patient);
                    patientRepository.save(patient);
            }
        });

        user.setRoles(roles);
        userRepository.save(user);

        return new ResponseEntity<>(new ResponseMessage("User registered successfully."), HttpStatus.OK);

    }

}
