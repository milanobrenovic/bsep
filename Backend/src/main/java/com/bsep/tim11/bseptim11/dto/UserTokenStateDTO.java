package com.bsep.tim11.bseptim11.dto;

public class UserTokenStateDTO {

    private String jwtAccessToken;
    private Long expiresIn;

    public UserTokenStateDTO() {
        this.jwtAccessToken = null;
        this.expiresIn = null;
    }

    public UserTokenStateDTO(String jwtAccessToken, long expiresIn) {
        this.jwtAccessToken = jwtAccessToken;
        this.expiresIn = expiresIn;
    }

    public void setJwtAccessToken(String jwtAccessToken) {
        this.jwtAccessToken = jwtAccessToken;
    }

    public String getJwtAccessToken() {
        return jwtAccessToken;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String toString() {
        return "UserTokenState{" +
                "jwtAccessToken='" + jwtAccessToken + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }

}
