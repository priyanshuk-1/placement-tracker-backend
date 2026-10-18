
package com.placement.placementtracker.repository;

import com.placement.placementtracker.entity.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findAllByOwner_Email(String email);

    Page<JobApplication> findAllByOwner_Email(
            String email, Pageable pageable);

    Optional<JobApplication> findByIdAndOwner_Email(
            Long id, String email);

    List<JobApplication> findAllByOwner_EmailAndStatus(
            String email, String status);

    List<JobApplication> findAllByOwner_EmailAndCompany(
            String email, String company);

    List<JobApplication> findAllByOwner_EmailAndRole(
            String email, String role);

    List<JobApplication> findAllByOwner_EmailAndCompanyAndStatus(
            String email, String company, String status);

    List<JobApplication> findAllByOwner_EmailOrderByCompanyAsc(
            String email);

    List<JobApplication> findAllByOwner_EmailAndRoleAndStatus(
            String email, String role, String status);

    List<JobApplication> findAllByOwner_EmailAndRoleAndCompany(
            String email, String role, String company);

    List<JobApplication> findAllByOwner_EmailAndCompanyContaining(
            String email, String company);

    List<JobApplication> findAllByOwner_EmailAndCompanyContainingIgnoreCase(
            String email, String company);

    @Query("""
        SELECT j FROM JobApplication j
        WHERE j.owner.email = :email
          AND (j.company = :company OR j.status = :status)
        """)
    List<JobApplication> findByOwnerAndCompanyOrStatus(
            @Param("email") String email,
            @Param("company") String company,
            @Param("status") String status);
}
