package com.placement.placementtracker.dto;
import jakarta.validation.constraints.NotBlank;

public class JobApplicationDTO {

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Status is required")
    private String status;

    public String getCompany() {
        return company;
    }
    public void setCompany(String company){
        this.company = company;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role){
        this.role = role;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }

}
