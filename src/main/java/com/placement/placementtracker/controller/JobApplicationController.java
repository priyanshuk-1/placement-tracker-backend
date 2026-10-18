
package com.placement.placementtracker.controller;

import com.placement.placementtracker.dto.JobApplicationDTO;
import com.placement.placementtracker.entity.JobApplication;
import com.placement.placementtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService service;

    public JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    // POST - Create
    @PostMapping
    public ResponseEntity<JobApplicationDTO> createJobApplication(
            @Valid @RequestBody JobApplicationDTO application) {

        JobApplicationDTO createdApplication =
                service.createApplication(application);

        return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
    }

    // GET - All applications owned by the logged-in user
    @GetMapping
    public List<JobApplication> getAllApplications() {
        return service.getAllApplications();
    }

    // GET - By ID
    @GetMapping("/{id}")
    public JobApplication getJobApplicationById(
            @PathVariable Long id) {
        return service.getApplicationById(id);
    }

    // PUT - Update
    @PutMapping("/{id}")
    public JobApplication updateJobApplication(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationDTO application) {
        return service.updateApplication(id, application);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJobApplication(@PathVariable Long id) {
        service.deleteJobApplication(id);
    }

    // GET - By Status
    @GetMapping(params = {"status", "!company", "!role"})
    public List<JobApplication> findByStatus(
            @RequestParam String status) {
        return service.findByStatus(status);
    }

    // GET - By Company + Status
    @GetMapping(params = {"company", "status"})
    public List<JobApplication> findByCompanyAndStatus(
            @RequestParam String company,
            @RequestParam String status) {
        return service.findByCompanyAndStatus(company, status);
    }

    // GET - By Company
    @GetMapping(params = {"company", "!status", "!role"})
    public List<JobApplication> findByCompany(
            @RequestParam String company) {
        return service.findByCompany(company);
    }

    // GET - By Role + Status
    @GetMapping(params = {"role", "status"})
    public List<JobApplication> findAllByRoleAndStatus(
            @RequestParam String role,
            @RequestParam String status) {
        return service.findAllByRoleAndStatus(role, status);
    }

    // GET - By Role + Company
    @GetMapping(params = {"role", "company"})
    public List<JobApplication> findAllByRoleAndCompany(
            @RequestParam String role,
            @RequestParam String company) {
        return service.findAllByRoleAndCompany(role, company);
    }

    // GET - By Role
    @GetMapping(params = {"role", "!status", "!company"})
    public List<JobApplication> findByRole(
            @RequestParam String role) {
        return service.findByRole(role);
    }

    // GET - Company OR Status
    @GetMapping("/or")
    public List<JobApplication> findByCompanyOrStatus(
            @RequestParam String company,
            @RequestParam String status) {
        return service.findByCompanyOrStatus(company, status);
    }

    // GET - Sorted
    @GetMapping("/sorted")
    public List<JobApplication> findAllByOrderByCompanyAsc() {
        return service.findAllByOrderByCompanyAsc();
    }

    // GET - Pagination
    @GetMapping("/page")
    public Page<JobApplication> getApplicationsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "company") String sortBy) {
        return service.getApplicationsPage(page, size, sortBy);
    }

    // GET - Search Company
    @GetMapping("/search")
    public List<JobApplication> findByCompanyContaining(
            @RequestParam String company) {
        return service.findByCompanyContaining(company);
    }

    // GET - Search Company Ignore Case
    @GetMapping("/search-ignore-case")
    public List<JobApplication> findByCompanyContainingIgnoreCase(
            @RequestParam String company) {
        return service.findByCompanyContainingIgnoreCase(company);
    }
}
