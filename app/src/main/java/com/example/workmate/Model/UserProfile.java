package com.example.workmate.Model;

public class UserProfile {
    private String name, surname, profession, location, educationStatus, gender, cvUrl, userId, profilResimleriUrl;

    public UserProfile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEducationStatus() {
        return educationStatus;
    }

    public void setEducationStatus(String educationStatus) {
        this.educationStatus = educationStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId(){return userId;}

    public String getProfilResimleriUrl() {
        return profilResimleriUrl;
    }

    public void setProfilResimleriUrl(String profilResimleriUrl) {
        this.profilResimleriUrl = profilResimleriUrl;
    }
}
