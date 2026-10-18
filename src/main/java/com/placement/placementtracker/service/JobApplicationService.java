
package com.placement.placementtracker.service;

import com.placement.placementtracker.dto.JobApplicationDTO;
import com.placement.placementtracker.entity.AppUser;
import com.placement.placementtracker.entity.JobApplication;
import com.placement.placementtracker.repository.AppUserRepository;
import com.placement.placementtracker.repository.JobApplicationRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final AppUserRepository userRepository;

    public JobApplicationService(
            JobApplicationRepository repository,
            AppUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // Get the email from the authenticated JWT user
    private String getCurrentUserEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().equals("anonymousUser")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please log in first"
            );
        }

        return authentication.getName();
    }

    // Find an application belonging to the current user
    private JobApplication getOwnedApplication(Long id) {
        return repository.findByIdAndOwner_Email(
                id, getCurrentUserEmail()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Application not found"
        ));
    }

    // POST - Create
    @Transactional
    public JobApplicationDTO createApplication(
            JobApplicationDTO application) {

        String email = getCurrentUserEmail();

        AppUser owner = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Authenticated user not found"
                ));

        JobApplication jobApplication = new JobApplication();
        jobApplication.setCompany(application.getCompany());
        jobApplication.setRole(application.getRole());
        jobApplication.setStatus(application.getStatus());
        jobApplication.setOwner(owner);

        JobApplication saved = repository.save(jobApplication);

        JobApplicationDTO response = new JobApplicationDTO();
        response.setCompany(saved.getCompany());
        response.setRole(saved.getRole());
        response.setStatus(saved.getStatus());

        return response;
    }

    // GET - All applications belonging to current user
    public List<JobApplication> getAllApplications() {
        return repository.findAllByOwner_Email(getCurrentUserEmail());
    }

    // GET - By ID, only if owned by current user
    public JobApplication getApplicationById(Long id) {
        return getOwnedApplication(id);
    }

    // PUT - Update only the current user's application
    @Transactional
    public JobApplication updateApplication(
            Long id,
            JobApplicationDTO application) {

        JobApplication existing = getOwnedApplication(id);

        existing.setCompany(application.getCompany());
        existing.setRole(application.getRole());
        existing.setStatus(application.getStatus());

        return repository.save(existing);
    }

    // DELETE - Delete only the current user's application
    @Transactional
    public void deleteJobApplication(Long id) {
        JobApplication existing = getOwnedApplication(id);
        repository.delete(existing);
    }

    // GET - By Status
    public List<JobApplication> findByStatus(String status) {
        return repository.findAllByOwner_EmailAndStatus(
                getCurrentUserEmail(), status);
    }

    // GET - By Company
    public List<JobApplication> findByCompany(String company) {
        return repository.findAllByOwner_EmailAndCompany(
                getCurrentUserEmail(), company);
    }

    // GET - By Role
    public List<JobApplication> findByRole(String role) {
        return repository.findAllByOwner_EmailAndRole(
                getCurrentUserEmail(), role);
    }

    // GET - Company + Status
    public List<JobApplication> findByCompanyAndStatus(
            String company, String status) {
        return repository.findAllByOwner_EmailAndCompanyAndStatus(
                getCurrentUserEmail(), company, status);
    }

    // GET - Company OR Status, restricted to current user
    public List<JobApplication> findByCompanyOrStatus(
            String company, String status) {
        return repository.findByOwnerAndCompanyOrStatus(
                getCurrentUserEmail(), company, status);
    }

    // GET - Sorted by company
    public List<JobApplication> findAllByOrderByCompanyAsc() {
        return repository.findAllByOwner_EmailOrderByCompanyAsc(
                getCurrentUserEmail());
    }

    // GET - Pagination and sorting, restricted to current user
    public Page<JobApplication> getApplicationsPage(
            int page, int size, String sortBy) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be non-negative and size must be between 1 and 100"
            );
        }

        // Allow only known entity fields for sorting
        if (!List.of("id", "company", "role", "status").contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort field"
            );
        }

        Pageable pageable = PageRequest.of(
                page, size, Sort.by(sortBy).ascending());

        return repository.findAllByOwner_Email(
                getCurrentUserEmail(), pageable);
    }

    // GET - Role + Status
    public List<JobApplication> findAllByRoleAndStatus(
            String role, String status) {
        return repository.findAllByOwner_EmailAndRoleAndStatus(
                getCurrentUserEmail(), role, status);
    }

    // GET - Role + Company
    public List<JobApplication> findAllByRoleAndCompany(
            String role, String company) {
        return repository.findAllByOwner_EmailAndRoleAndCompany(
                getCurrentUserEmail(), role, company);
    }

    // GET - Search company
    public List<JobApplication> findByCompanyContaining(String company) {
        return repository
                .findAllByOwner_EmailAndCompanyContaining(
                        getCurrentUserEmail(), company);
    }

    // GET - Search company, ignoring case
    public List<JobApplication> findByCompanyContainingIgnoreCase(
            String company) {
        return repository
                .findAllByOwner_EmailAndCompanyContainingIgnoreCase(
                        getCurrentUserEmail(), company);
    }
}
