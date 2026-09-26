package com.example.workmate.Model;

public class User {
    private String name, surname, userId, profilResimleriUrl;

    public User(){}

    public User(String name, String surname, String userId, String profilResimleriUrl){
        this.name = name;
        this.surname = surname;
        this.userId = userId;
        this.profilResimleriUrl = profilResimleriUrl;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilResimleriUrl() {
        return profilResimleriUrl;
    }

    public void setProfilResimleriUrl(String profilResimleriUrl) {
        this.profilResimleriUrl = profilResimleriUrl;
    }
}
