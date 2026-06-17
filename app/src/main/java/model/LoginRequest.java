package model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("UserName")
    private String username;
    
    @SerializedName("Password")
    private String password;

    @SerializedName("Captcha")
    private String captcha;

    @SerializedName("Token")
    private String token;

    @SerializedName("RememberMe")
    private boolean rememberMe;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
        this.captcha = "";
        this.token = "";
        this.rememberMe = false;
    }
}