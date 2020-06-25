package com.bsep.tim11.bseptim11.dto;

public class LoggedInUserDTO {

    private Long id;
    private String username;
    private String role;
    private UserTokenStateDTO userTokenState;

    public LoggedInUserDTO(Long id, String username, String role, UserTokenStateDTO userTokenState) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.userTokenState = userTokenState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserTokenStateDTO getUserTokenState() {
        return userTokenState;
    }

    public void setUserTokenState(UserTokenStateDTO userTokenState) {
        this.userTokenState = userTokenState;
    }

    @Override
    public String toString() {
        return "LoggedInUserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", userTokenState=" + userTokenState.toString() +
                '}';
    }

}
