package com.alkemy.mleon.prodmgmt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class UserDTO {

    @Id
    private  String id;

    @NotBlank
    private String name;

    @Email
    private String username;

    @Size(min=8 , max=20)
    private String password;

    private Set<String> roles;
}