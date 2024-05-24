package com.challenger.app.Model;

import java.util.List;

public class User {

    private String fullName;
    private String phoneNumber;
    private String email;

    private List<String> myChallangesId;

    private String profileImageUrl;


    public User() {
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getMyChallangesId() {
        return myChallangesId;
    }

    public void setMyChallangesId(List<String> myChallangesId) {
        this.myChallangesId = myChallangesId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
