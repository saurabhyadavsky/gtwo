package com.example.gtwo.notifications;

public class Token {
    private String token;

    public Token() {
        // Empty constructor required for Firebase Realtime Database deserialization
    }

    public Token(String token) {
        this.token = token;
    }

    // Getter and setter for the token field
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

