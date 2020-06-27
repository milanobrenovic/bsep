package com.bsep.tim11.bseptim11.dto;

import com.bsep.tim11.bseptim11.model.NormalUser;
import net.minidev.json.annotate.JsonIgnore;

import javax.validation.constraints.NotEmpty;

public class NormalUserDTO {

    private Long id;

    @NotEmpty(message = "Username is empty.")
    private String username;

    @JsonIgnore
    @NotEmpty(message = "Password is empty.")
    private String password;

    @NotEmpty(message = "Firstname is empty.")
    private String firstName;

    @NotEmpty(message = "Lastname is empty.")
    private String lastName;

    public NormalUserDTO() {

    }

    public NormalUserDTO(Long id, String username, String password, String firstName, String lastName) {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public NormalUserDTO(NormalUser normalUser) {
        this(normalUser.getId(), normalUser.getUsername(), normalUser.getPassword(),
                normalUser.getFirstName(), normalUser.getLastName());
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "NormalUserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

}
