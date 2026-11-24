package in.aman.jgate.management.service;

import in.aman.jgate.management.Exception.ApiException;
import in.aman.jgate.management.Repsitory.ManagementUserRepository;
import in.aman.jgate.management.dto.requestDto.LoginRequest;
import in.aman.jgate.management.dto.responseDto.LoginResponse;
import in.aman.jgate.management.entity.ManagementUser;
import in.aman.jgate.management.entity.OrganizationStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthenticationService {


   private final ManagementUserRepository managementUserRepository;
   private final PasswordEncoder passwordEncoder;
   private final JwtEncoder jwtEncoder;
   private final Long expirationSeconds;


   public AuthenticationService(ManagementUserRepository managementUserRepository,
                                PasswordEncoder passwordEncoder,
                                JwtEncoder jwtEncoder,
                                @Value("${security.jwt.expiration-seconds}") Long expirationSeconds){
       this.managementUserRepository = managementUserRepository;
       this.expirationSeconds = expirationSeconds;
       this.passwordEncoder = passwordEncoder;
       this.jwtEncoder = jwtEncoder;
   }


   @Transactional(readOnly = true)
   public LoginResponse login(LoginRequest loginRequest){

       System.out.println("Login Request " + loginRequest.toString());

      ManagementUser user = managementUserRepository.findByUsername(loginRequest.getUsername())
              .orElseThrow(() -> invalidCredentials());


       System.out.println("User fetched from db " + user.toString());

      if(!user.isEnabled() || !passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())){
          throw invalidCredentials();
      }

      if(user.getOrganization() != null &&
              user.getOrganization().getStatus() != OrganizationStatus.ACTIVE ){
          throw new ApiException(HttpStatus.FORBIDDEN, "Organisation is not active");
      }

      Instant issuedAt = Instant.now();
      String role = user.getRole().getName();
      Long organisationId = user.getOrganization() == null ? null : user.getOrganization().getId();

       JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
               .issuer("j-gate-api-management")
               .issuedAt(issuedAt)
               .expiresAt(issuedAt.plusSeconds(expirationSeconds))
               .subject(user.getUsername())
               .claim("role", role);

       if(organisationId != null){
           claims.claim("organisationId", organisationId);
       }

       JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
       String token = jwtEncoder.encode(
               JwtEncoderParameters.from(jwsHeader, claims.build())
       ).getTokenValue();

       return new LoginResponse(token, "Bearer", expirationSeconds, role, organisationId);
   }

   private RuntimeException invalidCredentials(){
       return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
   }

}
