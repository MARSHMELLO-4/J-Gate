package in.aman.jgate.management.controller;


import in.aman.jgate.management.dto.requestDto.OrganizationRequest;
import in.aman.jgate.management.dto.requestDto.OrganizationStatusRequest;
import in.aman.jgate.management.dto.requestDto.TenantAdminRequest;
import in.aman.jgate.management.dto.responseDto.OrganizationResponse;
import in.aman.jgate.management.dto.responseDto.TenantAdminResponse;
import in.aman.jgate.management.service.OrganizationService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform/organizations")
public class PlatformOrganisationController {

    private final OrganizationService organizationService;

    public PlatformOrganisationController(OrganizationService organizationService){
        this.organizationService = organizationService;
    }

    //Crud -> organization, tenant and admin

    //create
    @PostMapping
    public ResponseEntity<OrganizationResponse> create(
            @Valid @RequestBody OrganizationRequest request
    ) {
        return ResponseEntity.ok(organizationService.create(request));
    }

    //read all-> endpoint for reading all organizations
    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> findAll(){
        return ResponseEntity.ok(organizationService.findAll());
    }


    //reading single organization
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(organizationService.findById(id));
    }


    //update organization by id
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> update(@PathVariable Long id,
                                                       @Valid
                                                       @RequestBody
                                                       OrganizationRequest organizationRequest){
        return ResponseEntity.ok(organizationService.update(id, organizationRequest));
    }

    //status update
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrganizationResponse> chaseStatus(
            @PathVariable Long id,
            @Valid @RequestBody
            OrganizationStatusRequest request
    ) {
        return ResponseEntity.ok(organizationService.changeStatus(id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        organizationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //crud operations for tenant admins
    //posting the tenant admin
    @PostMapping("/{organizationId}/tenant-admin")
    public ResponseEntity<TenantAdminResponse> createTenantAdmin(
            @PathVariable Long organizationId,
            @Valid @RequestBody TenantAdminRequest tenantAdminRequest
            ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizationService.createTenantAdmin(organizationId, tenantAdminRequest));
    }

    @GetMapping("/{organizationId}/tenant-admin")
    public ResponseEntity<TenantAdminResponse> getTenantAdmin(
            @PathVariable Long organizationId
    ) {
        return ResponseEntity.ok(organizationService.getTenantAdminResponse(organizationId));
    }

    @PutMapping("/{organizationId}/tenant-admin")
    public ResponseEntity<TenantAdminResponse> updateTenantAdmin(
            @PathVariable Long organizationId,
            @Valid @RequestBody TenantAdminRequest request
    ){
        return ResponseEntity.ok(
                organizationService.updateTenantAdmin(organizationId, request)
        );
    }

    @DeleteMapping("/{organizationId}/tenant-admin")
    public ResponseEntity<Void> deleteTenantAdmin(
            @PathVariable Long organizationId
    ) {
        organizationService.deleteTenantAdmin(organizationId);
        return ResponseEntity.noContent().build();
    }

}
