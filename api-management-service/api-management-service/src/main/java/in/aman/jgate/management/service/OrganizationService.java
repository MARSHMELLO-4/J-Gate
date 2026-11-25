package in.aman.jgate.management.service;

import in.aman.jgate.management.Exception.ApiException;
import in.aman.jgate.management.Repsitory.OrganizationRepository;
import in.aman.jgate.management.dto.requestDto.OrganizationRequest;
import in.aman.jgate.management.dto.requestDto.OrganizationStatusRequest;
import in.aman.jgate.management.dto.responseDto.OrganizationResponse;
import in.aman.jgate.management.entity.Organization;
import in.aman.jgate.management.entity.OrganizationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository){
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public OrganizationResponse create(OrganizationRequest request){

        long startedAt = System.nanoTime();

        //this function will check if the slug and the name is not there in the database already
        checkUnique(request.getName(), request.getSlug(), null);

        //nnow we will create the organization object
        Organization organization = new Organization();

        organization.setName(request.getName());
        organization.setSlug(request.getSlug());
        organization.setStatus(OrganizationStatus.PENDING);
        organization.setCreatedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());

        organizationRepository.save(organization); //saving the created organization object in the database

        //now we have to return this in the form of organization response dto, the method for this is
        // already created in the entity class itself
        return OrganizationResponse.from(organization);
    }

    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll(){
        List<OrganizationResponse> response = new ArrayList<>();


        //getting the list of the organizations from the db in which the status is not deleted.
        for(Organization organization : organizationRepository
                .findAllByStatusNot(OrganizationStatus.DELETED)){
            response.add(OrganizationResponse.from(organization));
        }

        return response;
    }

    @Transactional(readOnly = true)
    public OrganizationResponse findById(Long id){
        return OrganizationResponse.from(getOrganization(id));
    }

    @Transactional
    public OrganizationResponse update(Long id, OrganizationRequest request){
        long startedAt = System.nanoTime();

        Organization organization = getOrganization(id);

        checkUnique(request.getName(), request.getSlug(), id);
        organization.setName(request.getName());
        organization.setSlug(request.getSlug());
        organization.setUpdatedAt(LocalDateTime.now());

        return OrganizationResponse.from(organization);
    }

    @Transactional
    public OrganizationResponse changeStatus(Long id, OrganizationStatusRequest request){
        long startedAt = System.nanoTime();
        Organization organization = getOrganization(id);

        if(request.getStatus() == OrganizationStatus.DELETED){
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Use DELETE to soft-delete an organization");
        }

        organization.setStatus(request.getStatus());
        organization.setUpdatedAt(LocalDateTime.now());

        return OrganizationResponse.from(organization);
    }

    @Transactional
    public void delete(Long id){
        long startedAt = System.nanoTime();

        Organization organization = getOrganization(id);
        organization.setStatus(OrganizationStatus.DELETED);
        organization.setDeletedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());
    }

    private Organization getOrganization(Long id){
        return organizationRepository.findByIdAndStatusNot(id, OrganizationStatus.DELETED)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Organization not found"));
    }

    private void checkUnique(String name, String slug, Long currentId){
        if(organizationRepository.existsByNameIgnoreCase(name)){
            Organization current
                    =  currentId == null ? null
                    : organizationRepository
                    .findById(currentId)
                    .orElse(null);

            if(currentId == null || !current.getName().equalsIgnoreCase(name)){
                throw new ApiException(HttpStatus.CONFLICT, "Organization name already exists");
            }
        }

        if(organizationRepository.existsBySlugIgnoreCase(slug)){
            Organization current
                    = currentId == null ? null
                    : organizationRepository.findById(currentId).orElse(null);

            if(current == null || !current.getSlug().equalsIgnoreCase(slug)){
                throw new ApiException(HttpStatus.CONFLICT, "Organization with the slug already exists");
            }
        }
    }

}
