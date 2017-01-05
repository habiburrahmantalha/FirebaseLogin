package com.tree.firebaselogin;

public class User {
    public String username;
    public String email;
    public String time;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }



    public User(String username, String email, String time) {
        this.username = username;
        this.email = email;
        this.time = time;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
