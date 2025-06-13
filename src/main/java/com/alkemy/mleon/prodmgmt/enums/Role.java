package com.alkemy.mleon.prodmgmt.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, ADMIN;
    public String getName(){return this.name();}

    @Override
    public String getAuthority() {
        return name();
    }
}