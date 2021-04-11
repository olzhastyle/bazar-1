package com.example.baza_r;

public class FindPeople {
    public String profileImage, fullname, status;

    public FindPeople() {

    }

    public FindPeople(String profileImage, String fullname, String status){
        this.profileImage = profileImage;
        this.fullname = fullname;
        this.status = status;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
