package com.example.workmate.Model;

import java.util.List;

public class Job {

    private String jobID, jobTitle, company, location, description, jobType,locationType;
    private String currency, requirements, contactEmail, contactPhone, userId, documentId;
    private Integer minSalary, maxSalary;
    private boolean isSaved;
    public Job(){}
    private List<String> applicants;

    public Job(String jobID, String jobTitle, String company, String location, String description,
               String jobType,String locationType, Integer minSalary, Integer maxSalary, String currency,
               String requirements, String contactEmail, String contactPhone,boolean isSaved,String userId,List<String> applicants, String documentId) {
        this.jobID = jobID;
        this.jobTitle = jobTitle;
        this.company = company;
        this.location = location;
        this.description = description;
        this.jobType = jobType;
        this.locationType = locationType;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.currency = currency;
        this.requirements = requirements;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.isSaved = isSaved;
        this.userId = userId;
        this.applicants = applicants;
        this.documentId = documentId;
    }

    public void setJobID(String jobID){
        this.jobID = jobID;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public void setLocationType(String locationType){
        this.locationType = locationType;
    }

    public void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public void setMaxSalary(Integer maxSalary) {
        this.maxSalary = maxSalary;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setSaved(boolean isSaved){
        this.isSaved = isSaved;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setApplicants(List<String> applicants){
        this.applicants = applicants;
    }

    public void setDocumentId(String documentId){
        this.documentId = documentId;
    }

    // Getter'lar
    public String getJobID(){return jobID;}
    public String getJobTitle() { return jobTitle; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getJobType() { return jobType; }
    public String getLocationType(){ return locationType;}
    public Integer getMinSalary() { return minSalary; }
    public Integer getMaxSalary() { return maxSalary; }
    public String getCurrency() { return currency; }
    public String getRequirements() { return requirements; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public boolean isSaved(){ return isSaved;}
    public String getUserId(){ return userId;}
    public List<String> getApplicants(){ return applicants;}
    public String getDocumentId(){ return documentId;}
}
