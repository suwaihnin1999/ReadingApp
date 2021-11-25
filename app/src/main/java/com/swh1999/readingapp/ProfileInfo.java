package com.swh1999.readingapp;

public class ProfileInfo {
    private String email;
    private String pass;
    private String username;
    private String profileImg;
    private String backImg;
    private String gender;
    private String birthDay;

    public ProfileInfo(String email, String pass, String username, String profileImg, String backImg, String gender, String birthDay) {
        this.email = email;
        this.pass = pass;
        this.username = username;
        this.profileImg = profileImg;
        this.backImg = backImg;
        this.gender = gender;
        this.birthDay = birthDay;
    }

    public ProfileInfo() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getBackImg() {
        return backImg;
    }

    public void setBackImg(String backImg) {
        this.backImg = backImg;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }
}
