package in.aman.jgate.management.controller;

import in.aman.jgate.management.dto.responseDto.HealthResponse;
import in.aman.jgate.management.service.HealthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    private String serviceName;
    private String serviceDescription;

    public HealthService healthService;

    public HealthController(@Value("${info.app.name}") String serviceName,
                            @Value("${info.app.description}") String serviceDescription,
                            HealthService healthService){
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.healthService = healthService;
    }

    @GetMapping
    public ResponseEntity<HealthResponse> health(){
        String dbStatus = healthService.getHealth();
        return ResponseEntity.ok(
                new HealthResponse(serviceName, serviceDescription, dbStatus));
    }


}
