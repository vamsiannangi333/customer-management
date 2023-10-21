package com.example.CustomerManagement.entities;

public class User {
    private String login_id;
    // private String login_id="test@sunbasedata.com";
    private String password;
    // private String password="Test@123";


    public User(String login_id, String password) {
        this.login_id = login_id;
        this.password = password;
    }

    public User() {

    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
